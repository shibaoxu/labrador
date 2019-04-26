package com.lablador.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.DockerInfo
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.extras.DockerWaitHealthyContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage
import com.lablador.docker.tasks.DockerSetupNetwork
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.MapProperty

class DockerCommonPlugin implements Plugin<Project> {
    private static final String INFO_TASK_NAME = 'dockerInfo'
    private static final String REMOVE_BASE_IMAGE = 'dockerRemoveBaseImage'
    private static final String BUILD_BOOT_BASE_IMAGE_TASK_NAME = 'dockerSetupBaseImage'
    private static final String SETUP_NETWORK_TASK_NAME = 'dockerSetupNetwork'
    private static final String BUILD_MYSQL_IMAGE_TASK_NAME = 'dockerSetupMysqlImage'
    private static final String REMOVE_MYSQL_IMAGE = 'dockerRemoveMysqlImage'
    private static final String CREATE_MYSQL_CONTAINER = 'dockerCreateMysqlContainer'
    private static final String START_MYSQL_CONTAINER = 'dockerStartMysqlContainer'
    private static final String CHECK_MYSQL_CONTAINER = 'dockerCheckMysqlContainer'
    private static final String SETUP_TASK_NAME = 'dockerSetup'
    private static final String STARTUP_CLOUD_SERVER = 'dockerStartCloudServer'

    @Override
    void apply(Project project) {
        project.plugins.apply(DockerRemoteApiPlugin)

        project.tasks.create(INFO_TASK_NAME, DockerInfo)

        DockerRemoveImage removeBootBaseImage = createRemoveBaseImageTask(project)
        DockerBuildImage setupBaseImage = createBuildBootBaseImageTask(project)
        setupBaseImage.dependsOn removeBootBaseImage

        DockerSetupNetwork setupNetwork = createSetupNetworkTask(project)

        DockerRemoveImage removeMysql = createRemoveMysqlImageTask(project)
        DockerBuildImage setupMysql = createBuildMySqlImageTask(project)
        setupMysql.dependsOn(removeMysql)

        DockerCreateContainer createMysqlContainer = createMysqlContainerTask(project)
        DockerStartContainer startMysqlContainer = createStartMysqlContainerTask(project)
        startMysqlContainer.dependsOn createMysqlContainer
        DockerWaitHealthyContainer checkMysqlContainer = createMysqlHealthyCheckTask(project)
        checkMysqlContainer.dependsOn startMysqlContainer

        Task startCloudServer = createStartupCloudServerTask(project)

        project.tasks.create(SETUP_TASK_NAME){
            group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
            description = '初始化docker网络，并构建基础镜像'
            dependsOn setupBaseImage, setupMysql, setupNetwork
        }
    }

    private static DockerInfo createDockerInfoTask(Project project){
        project.tasks.create(INFO_TASK_NAME, DockerInfo, new Action<DockerInfo>() {
            @Override
            void execute(DockerInfo dockerInfo) {
                dockerInfo.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '查看docker相关信息'
                }
            }
        })
    }

    private static DockerRemoveImage createRemoveBaseImageTask(Project project){
        project.tasks.create(REMOVE_BASE_IMAGE, DockerRemoveImage, new Action<DockerRemoveImage>() {
            @Override
            void execute(DockerRemoveImage dockerRemoveImage) {
                dockerRemoveImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '删除Spring boot的基础镜像'
                    imageId = project.dockerBootBaseImage
                    onError{exception ->
                        if (exception.message.contains("No such image")){
                            logger.info("待删除的镜像:${imageId}不存在")
                        }else{
                            throw exception
                        }
                    }
                }
            }
        })
    }

    private static DockerBuildImage createBuildBootBaseImageTask(Project project){
        project.tasks.create(BUILD_BOOT_BASE_IMAGE_TASK_NAME, DockerBuildImage, new Action<DockerBuildImage>() {
            @Override
            void execute(DockerBuildImage dockerBuildImage) {
                dockerBuildImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建Spring boot应用的基础镜像'
                    inputDir = new File(project.projectDir, 'docker/baseimage')
                    tags.add(project.dockerBootBaseImage)
                }
            }
        })
    }

    private static DockerSetupNetwork createSetupNetworkTask(Project project){
        project.tasks.create(SETUP_NETWORK_TASK_NAME, DockerSetupNetwork, new Action<DockerSetupNetwork>() {
            @Override
            void execute(DockerSetupNetwork dockerSetupNetwork) {
                dockerSetupNetwork.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建docker环境的网络'
                    networkId = project.dockerNetwork
                }
            }
        })
    }

    private static DockerBuildImage createBuildMySqlImageTask(Project project){
        project.tasks.create(BUILD_MYSQL_IMAGE_TASK_NAME, DockerBuildImage, new Action<DockerBuildImage>() {
            @Override
            void execute(DockerBuildImage dockerBuildImage) {
                dockerBuildImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建MySql镜像'
                    inputDir = new File(project.projectDir, 'docker/mysqlimage')
                    tags.add("${project.dockerRegistry}/${project.name}-mysql:latest")
                }
            }
        })
    }

    private static DockerRemoveImage createRemoveMysqlImageTask(Project project){
        project.tasks.create(REMOVE_MYSQL_IMAGE, DockerRemoveImage, new Action<DockerRemoveImage>() {
            @Override
            void execute(DockerRemoveImage dockerRemoveImage) {
                dockerRemoveImage.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '删除MySql镜像'
                    imageId = "${project.dockerRegistry}/${project.name}-mysql:latest"
                    onError{exception ->
                        if (exception.message.contains("No such image")){
                            logger.info("待删除的镜像:${imageId}不存在")
                        }else{
                            throw exception
                        }
                    }
                }
            }
        })
    }

    private static DockerCreateContainer createMysqlContainerTask(Project project){
        project.tasks.create(CREATE_MYSQL_CONTAINER, DockerCreateContainer, new Action<DockerCreateContainer>() {
            @Override
            void execute(DockerCreateContainer dockerCreateContainer) {
                dockerCreateContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '创建MySQL镜像'
                    imageId = "${project.dockerRegistry}/${project.name}-mysql:latest"
                    portBindings = ['3306:3306']
                    network = "${project.dockerNetwork}".toString()
                    MapProperty<String, String> env = project.objects.mapProperty(String, String)
                    env.put('MYSQL_ROOT_PASSWORD', 'password')
                    envVars = env
                    containerName = "mysqlservice"
                    autoRemove = true
                }
            }
        })
    }

    private static DockerStartContainer createStartMysqlContainerTask(Project project){
        project.tasks.create(START_MYSQL_CONTAINER, DockerStartContainer, new Action<DockerStartContainer>() {
            @Override
            void execute(DockerStartContainer dockerStartContainer) {
                dockerStartContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '启动Mysql容器'
                    containerId = ((DockerCreateContainer)project.tasks.getByPath(CREATE_MYSQL_CONTAINER)).getContainerId()
                }
            }
        })
    }

    private static DockerWaitHealthyContainer createMysqlHealthyCheckTask(Project project){
        project.tasks.create(CHECK_MYSQL_CONTAINER, DockerWaitHealthyContainer, new Action<DockerWaitHealthyContainer>() {
            @Override
            void execute(DockerWaitHealthyContainer dockerWaitHealthyContainer) {
                dockerWaitHealthyContainer.with {
                    group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
                    description = '检查MySQL容器的健康状态'
                    containerId = ((DockerCreateContainer)project.tasks.getByPath(CREATE_MYSQL_CONTAINER)).getContainerId()
                }
            }
        })
    }

    private static Task createStartupCloudServerTask(Project project){
        project.tasks.create(STARTUP_CLOUD_SERVER){
            group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
            description = '启动spring cloud相关的service(eureka,config,zuul)及mysql'

            System.setProperty('spring.profiles.active', 'local')

            dependsOn "${CHECK_MYSQL_CONTAINER}"
//            dependsOn ":eurekaservice:${DockerSpringBootPlugin.HEALTH_CHECK_TASK_NAME}"
//            dependsOn ":configservice:${DockerSpringBootPlugin.HEALTH_CHECK_TASK_NAME}"
            dependsOn ":zuulservice:${DockerSpringBootPlugin.HEALTH_CHECK_TASK_NAME}"
        }
    }
}
