package com.lablador.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
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

    @Override
    void apply(Project project) {
        project.plugins.apply(DockerRemoteApiPlugin)
        DockerExtension dockerExtension = project.extensions.getByType(DockerExtension)

        DockerSpringBootApplication dockerSpringBootApplication = configureExtension(project, dockerExtension)


        project.plugins.withType(JavaPlugin){
            project.plugins.withId('org.springframework.boot'){
                Sync buildContextTask = createBuildContextTask(project)
                Dockerfile createDockerfile = createDockerFileTask(project, dockerSpringBootApplication)
                createDockerfile.dependsOn buildContextTask

                DockerRemoveImage dockerRemoveImage = createRemoveImageTask(project, dockerSpringBootApplication)
                DockerBuildImage dockerBuildImage = createBuildImageTask(project, dockerSpringBootApplication)
                dockerBuildImage.dependsOn createDockerfile, dockerRemoveImage

                DockerCreateContainer dockerCreateContainer = createCreateContainerTask(project, dockerSpringBootApplication)
                dockerCreateContainer.dependsOn dockerBuildImage
            }
        }
    }

    private static DockerSpringBootApplication configureExtension(Project project, DockerExtension dockerExtension){
        ((ExtensionAware)dockerExtension).extensions.create(SPRING_BOOT_EXTENSION_NAME, DockerSpringBootApplication, project)
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
    private static Dockerfile createDockerFileTask(Project project, DockerSpringBootApplication dockerSpringBootApplication){
        project.tasks.create(DOCKERFILE_TASK_NAME, Dockerfile, new Action<Dockerfile>() {
            @Override
            void execute(Dockerfile dockerfile) {
                dockerfile.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '为Spring boot应用创建dockerfile'
                    from(project.provider(new Callable<Dockerfile.From>() {
                        @Override
                        Dockerfile.From call() throws Exception {
                            new Dockerfile.From(dockerSpringBootApplication.baseImage.get())
                        }
                    }))
                    workingDir('/app')
                    copyFile('app/*.jar', '.')
                    entryPoint('java', '-jar')
                    if (dockerSpringBootApplication.profile.getOrNull()){
                        defaultCommand("-D${dockerSpringBootApplication.PROFILE_PROPERTY}=${dockerSpringBootApplication.profile.get()}",
                                "/app/${project.name}-${project.version}.jar")
                    }else{
                        defaultCommand("/app/${project.name}-${project.version}.jar")
                    }

                    if (dockerSpringBootApplication.ports.get()){
                        instruction("HEALTHCHECK CMD curl -f --silent http://localhost:${dockerSpringBootApplication.ports.get()[0]}/actuator/health || exit 1")
                    }
                    exposePort(dockerSpringBootApplication.ports)
                }
            }
        })
    }

    private static DockerBuildImage createBuildImageTask(Project project, DockerSpringBootApplication dockerSpringBootApplication){
        project.tasks.create(BUILD_IMAGE_TASK_NAME, DockerBuildImage, new Action<DockerBuildImage>() {
            @Override
            void execute(DockerBuildImage dockerBuildImage) {
                dockerBuildImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '为Spring boot应用创建docker镜像'
                    tags.add(Utils.determinImageTag(project, dockerSpringBootApplication))
                }
            }
        })

    }
    private static DockerRemoveImage createRemoveImageTask(Project project, DockerSpringBootApplication dockerSpringBootApplication){
        project.tasks.create(REMOVE_IMAGE_TASK_NAME, DockerRemoveImage, new Action<DockerRemoveImage>() {
            @Override
            void execute(DockerRemoveImage dockerRemoveImage) {
                dockerRemoveImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '在构建新镜像之前删除具有同样tag的镜像'
                    imageId = Utils.determinImageTag(project, dockerSpringBootApplication)
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

    private static DockerCreateContainer createCreateContainerTask(Project project, DockerSpringBootApplication dockerSpringBootApplication){
        project.tasks.create(CREATE_CONTAINER_TASK_NAME, DockerCreateContainer, new Action<DockerCreateContainer>() {
            @Override
            void execute(DockerCreateContainer dockerCreateContainer) {
                dockerCreateContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建Spring boot应用容器'
                    targetImageId Utils.determinImageTag(project, dockerSpringBootApplication)
                    portBindings = dockerSpringBootApplication.bindPorts
                    containerName = "${project.name}".toString()
                    network = "${project.dockerNetwork}".toString()
                    autoRemove = true

                    if(dockerSpringBootApplication.profile == 'local' && !project.name.equals('eurekaservice')) {
                        cmd = [
                                "-jar",
                                "-Dspring.profiles.active=${dockerSpringBootApplication.profile.get()}",
                                "-Deureka.client.service-url.defaultZone=http://eurekaservice:${Utils.getAppPorts(project(':eurekaservice'))[0]}/eureka/".toString(),
                                "/app/${project.name}-${project.version}.jar".toString()
                        ]
                    }

                }
            }
        })
    }

}
