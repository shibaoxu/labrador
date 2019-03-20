package com.lablador.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.DockerInfo
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage
import com.lablador.docker.tasks.DockerSetupNetwork
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerCommonPlugin implements Plugin<Project> {
    private static final String INFO_TASK_NAME = 'dockerInfo'
    private static final String REMOVE_BASE_IMAGE = 'dockerRemoveBaseImage'
    private static final String BUILD_BOOT_BASE_IMAGE_TASK_NAME = 'dockerSetupBaseImage'
    private static final String SETUP_NETWORK_TASK_NAME = 'dockerSetupNetwork'
    private static final String SETUP_TASK_NAME = 'dockerSetup'

    @Override
    void apply(Project project) {
        project.plugins.apply(DockerRemoteApiPlugin)

        project.tasks.create(INFO_TASK_NAME, DockerInfo)

        DockerRemoveImage removeBootBaseImage = createRemoveBaseImageTask(project)

        DockerBuildImage setupBaseImage = createBuildBootBaseImageTask(project)
        setupBaseImage.dependsOn removeBootBaseImage

        DockerSetupNetwork setupNetwork = createSetupNetworkTask(project)

        project.tasks.create(SETUP_TASK_NAME){
            group = DockerRemoteApiPlugin.DEFAULT_TASK_GROUP
            description = '初始化docker网络，并构建基础镜像'
            dependsOn setupBaseImage, setupNetwork
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
}
