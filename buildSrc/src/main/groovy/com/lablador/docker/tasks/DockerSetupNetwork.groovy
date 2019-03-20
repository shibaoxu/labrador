package com.lablador.docker.tasks

import com.bmuschko.gradle.docker.tasks.network.DockerExistingNetwork
import com.github.dockerjava.api.DockerClient

class DockerSetupNetwork extends DockerExistingNetwork {
    @Override
    void runRemoteCommand(DockerClient dockerClient) {
        logger.quiet "Set up network: '${networkId.get()}'."
        def isExisted = false
        try{
            dockerClient.inspectNetworkCmd().withNetworkId(networkId.get()).exec()
            isExisted = true
            logger.quiet("${networkId.get()}已经存在")
        } catch (exception){
            println 'network not existed.'
        }

        if (isExisted){
            logger.quiet("删除网络:${networkId.get()}")
            dockerClient.removeNetworkCmd(networkId.get()).exec()
        }

        logger.quiet("创建网络: ${networkId.get()}")
        dockerClient.createNetworkCmd().withName(networkId.get()).exec()

        if (nextHandler) {
            nextHandler.execute(network)
        }
    }
}
