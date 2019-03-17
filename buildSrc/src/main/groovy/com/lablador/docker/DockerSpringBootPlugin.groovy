package com.lablador.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerSpringBootPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(DockerRemoteApiPlugin)
        DockerExtension dockerExtension = project.extensions.getByType(DockerExtension)
    }
}
