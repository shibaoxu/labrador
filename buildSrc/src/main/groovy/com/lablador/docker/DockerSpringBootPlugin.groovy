package com.lablador.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.extras.DockerWaitHealthyContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Sync

import java.util.concurrent.Callable

class DockerSpringBootPlugin implements Plugin<Project> {
    public static final SPRING_BOOT_EXTENSION_NAME = 'springBoot'
    public static final BUILD_CONTEXT_TASK_NAME = 'dockerBuildContext'
    public static final DOCKERFILE_TASK_NAME = 'dockerCreateDockerfile'
    public static final BUILD_IMAGE_TASK_NAME = 'dockerBuildImage'
    public static final REMOVE_IMAGE_TASK_NAME = 'dockerRemoveImage'
    public static final CREATE_CONTAINER_TASK_NAME = 'dockerCreateContainer'
    public static final START_CONTAINER_TASK_NAME = 'dockerStartContainer'
    public static final HEALTH_CHECK_TASK_NAME = 'dockerContainerHealthCheck'

    @Override
    void apply(Project project) {
        project.plugins.apply(DockerRemoteApiPlugin)
        DockerExtension dockerExtension = project.extensions.getByType(DockerExtension)

        DockerSpringBoot dockerSpringBoot = configureExtension(project, dockerExtension)

        project.plugins.withType(JavaPlugin){
            project.plugins.withId('org.springframework.boot'){
                Sync buildContextTask = createBuildContextTask(project)
                Dockerfile createDockerfile = createDockerFileTask(project, dockerSpringBoot)
                createDockerfile.dependsOn buildContextTask

                DockerRemoveImage dockerRemoveImage = createRemoveImageTask(project, dockerSpringBoot)
                DockerBuildImage dockerBuildImage = createBuildImageTask(project, dockerSpringBoot)
                dockerBuildImage.dependsOn createDockerfile, dockerRemoveImage

                DockerCreateContainer dockerCreateContainer = createCreateContainerTask(project, dockerSpringBoot)
                dockerCreateContainer.dependsOn dockerBuildImage

                DockerStartContainer dockerStartContainer = createStartContainerTask(project)
                dockerStartContainer.dependsOn dockerCreateContainer
                dockerStartContainer.containerId =  dockerCreateContainer.getContainerId()

                DockerWaitHealthyContainer dockerWaitHealthyContainer = createHealthCheckTask(project)
                dockerWaitHealthyContainer.dependsOn dockerStartContainer
                dockerWaitHealthyContainer.containerId = dockerStartContainer.getContainerId()

            }
        }
    }

    private static DockerSpringBoot configureExtension(Project project, DockerExtension dockerExtension){
        ((ExtensionAware)dockerExtension).extensions.create(SPRING_BOOT_EXTENSION_NAME, DockerSpringBoot, project)
    }

    private static Sync createBuildContextTask(Project project){
        project.tasks.create(BUILD_CONTEXT_TASK_NAME, Sync, new Action<Sync>() {
            @Override
            void execute(Sync sync) {
                sync.with{
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '拷贝boorJar的构建结果到docker目录.'
                    dependsOn project.tasks.getByName('bootJar')
                    into(project.layout.buildDirectory.dir('docker/app'))
                    from(project.layout.buildDirectory.dir('libs'))
                }
            }
        })
    }
    private static Dockerfile createDockerFileTask(Project project, DockerSpringBoot dockerSpringBoot){
        project.tasks.create(DOCKERFILE_TASK_NAME, Dockerfile, new Action<Dockerfile>() {
            @Override
            void execute(Dockerfile dockerfile) {
                dockerfile.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '为Spring boot应用创建dockerfile'
                    from(project.provider(new Callable<Dockerfile.From>() {
                        @Override
                        Dockerfile.From call() throws Exception {
                            new Dockerfile.From(dockerSpringBoot.baseImage.get())
                        }
                    }))
                    workingDir('/app')
                    copyFile('app/*.jar', '.')
                    entryPoint('java', '-jar')
                    defaultCommand("/app/${project.name}-${project.version}.jar")

                    if (dockerSpringBoot.ports.get()){
//                        instruction("HEALTHCHECK CMD curl -f --silent http://localhost:${dockerSpringBoot.ports.get()[0]}/actuator/health || exit 1")
                        instruction("HEALTHCHECK --interval=2s --timeout=2s CMD curl -f --silent http://localhost:${dockerSpringBoot.ports.get()[0]}/actuator/health || exit 1")
                    }
                    exposePort(dockerSpringBoot.ports)
                }
            }
        })
    }

    private static DockerBuildImage createBuildImageTask(Project project, DockerSpringBoot dockerSpringBoot){
        project.tasks.create(BUILD_IMAGE_TASK_NAME, DockerBuildImage, new Action<DockerBuildImage>() {
            @Override
            void execute(DockerBuildImage dockerBuildImage) {
                dockerBuildImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '为Spring boot应用创建docker镜像'
                    tags.add(Utils.determinImageTag(project, dockerSpringBoot))
                }
            }
        })
    }
    private static DockerRemoveImage createRemoveImageTask(Project project, DockerSpringBoot dockerSpringBoot){
        project.tasks.create(REMOVE_IMAGE_TASK_NAME, DockerRemoveImage, new Action<DockerRemoveImage>() {
            @Override
            void execute(DockerRemoveImage dockerRemoveImage) {
                dockerRemoveImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '在构建新镜像之前删除具有同样tag的镜像'
                    imageId = Utils.determinImageTag(project, dockerSpringBoot)
                    onError { exception ->
                        if (!exception.message.contains('No such image')){
                            throw exception
                        } else {
                            logger.info("镜像:${imageId.get()}不存在")
                        }
                    }
                    onComplete{
                        logger.quiet("成功删除镜像${imageId.get()}")
                    }
                }
            }
        })
    }

    private static DockerCreateContainer createCreateContainerTask(Project project, DockerSpringBoot dockerSpringBoot){
        project.tasks.create(CREATE_CONTAINER_TASK_NAME, DockerCreateContainer, new Action<DockerCreateContainer>() {
            @Override
            void execute(DockerCreateContainer dockerCreateContainer) {
                dockerCreateContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建Spring boot应用容器'
                    targetImageId Utils.determinImageTag(project, dockerSpringBoot)
                    portBindings = dockerSpringBoot.bindPorts
                    containerName = "${project.name}".toString()
                    network = "${project.dockerNetwork}".toString()
                    autoRemove = true
                    cmd.set(determineContainerCommand(project, dockerSpringBoot))
                }
            }
        })
    }

    private static DockerStartContainer createStartContainerTask(Project project){
        project.tasks.create(START_CONTAINER_TASK_NAME, DockerStartContainer, new Action<DockerStartContainer>() {
            @Override
            void execute(DockerStartContainer dockerStartContainer) {
                dockerStartContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '启动Spring boot应用容器'
                }
            }
        })
    }

    private static DockerWaitHealthyContainer createHealthCheckTask(Project project){
        project.tasks.create(HEALTH_CHECK_TASK_NAME, DockerWaitHealthyContainer, new Action<DockerWaitHealthyContainer>() {
            @Override
            void execute(DockerWaitHealthyContainer dockerWaitHealthyContainer) {
                dockerWaitHealthyContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '检查Spring boot容器健康状态'
                    checkInterval = 1000
                    awaitStatusTimeout = 30
                }
            }
        })
    }


    private static ArrayList<String> determineContainerCommand(Project project, DockerSpringBoot dockerSpringBoot){
        def command = new ArrayList<String>()
        if(dockerSpringBoot.profile.getOrNull()){
            command.add("-Dspring.profiles.active=${dockerSpringBoot.profile.get()}")
        }
        command.add("-Dserver.port=${Utils.getAppPorts(project)[0]}")

        def eurekaPort = Utils.getAppPorts(project.rootProject.findProject('eurekaservice'))[0]
        if(dockerSpringBoot.profile.getOrNull() == 'local' && !project.name.equals('eurekaservice')) {
            command.add("-Deureka.client.service-url.defaultZone=http://eurekaservice:${eurekaPort}/eureka/")
            command.add("-Ddbserver=mysqlservice")
        }
        command.add("/app/${project.name}-${project.version}.jar")
        return command
    }
}
