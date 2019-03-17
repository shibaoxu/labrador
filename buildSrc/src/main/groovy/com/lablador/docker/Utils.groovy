package com.lablador.docker

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Provider

import java.util.concurrent.Callable

class Utils {
    static Provider<String> determinImageTag(Project project, DockerSpringBootApplication dockerSpringBootApplication){
        project.provider(new Callable<String>() {
            @Override
            String call() throws Exception {
                if (dockerSpringBootApplication.tag.getOrNull()){
                    return dockerSpringBootApplication.tag.get()
                }
                String tagVersion = project.version == 'unspecified' ? 'latest' : project.version
                String artifactAndVersion = "${project.name}:${tagVersion}".toLowerCase().toString()
                return artifactAndVersion
            }
        })
    }

    static ArrayList<Integer> getAppPorts(project){
        def appPort = "${project.name}AppPorts"
        def defaultAppPort = 'defaultAppPorts'

        if (project.hasProperty(appPort)){
            if (validateAppPorts(project[appPort])){
                return project[appPort].split(",").collect{it.trim().toInteger()}
            }else{
                throw new GradleException("${appPort}配置错误，端口号只能是整数。")
            }
        }else{
            if (project.hasProperty(defaultAppPort)){
                if (validateAppPorts(project[defaultAppPort])){
                    project.logger.info("没有配置${appPort},使用默认的appPort配置${project[defaultAppPort]}")
                    return project[defaultAppPort].split(",").collect{it.trim().toInteger()}
                }else{
                    throw new GradleException("${defaultAppPort}配置错误，端口号只能是整数。")
                }
            }else{
                project.logger.info('应用没有配置应用程序端口,使用默认端口8080')
                return [8080]
            }
        }
    }

    private static boolean validateAppPorts(ports){
        return !ports.isAllWhitespace() && ports.split(",").findAll{!it.isInteger()}.isEmpty()
    }

    private static boolean validateBindPorts(bindPorts){
        return bindPorts.split(",").findAll {
            !it.split(":").findAll{!it.trim().isInteger()}.isEmpty()
        }.isEmpty()
    }
    static ArrayList<String> getAppBindPorts(project){
        def bindPort = "${project.name}BindPorts"
        if (project.hasProperty(bindPort)){
            if (validateBindPorts(project[bindPort])){
                return project[bindPort].split(",").collect{it}
            }else{
                throw new GradleException("${bindPort}配置错误，绑定端口配置格式为8080:8080,9090:9090.")
            }
        }
        return []
    }
}
