package com.lablador.docker

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

class DockerSpringBootApplication {
    final Property<String> baseImage
    final Property<String> tag
    final ListProperty<Integer> ports
    final ListProperty<String> bindPorts
    final Property<String> profile
    public final String PROFILE_PROPERTY = 'spring.profiles.active'

    DockerSpringBootApplication(Project project){
        baseImage = project.objects.property(String)
        baseImage.set("${project.dockerBootBaseImage}")

        ports  = project.objects.listProperty(Integer)
        ports.set(Utils.getAppPorts(project))

        bindPorts = project.objects.listProperty(String)
        bindPorts.set(Utils.getAppBindPorts(project))

        tag = project.objects.property(String)
        tag.set("$project.name:$project.version")

        profile = project.objects.property(String)
        if (System.getProperty(PROFILE_PROPERTY) != null)
            if (!System.getProperty(PROFILE_PROPERTY).isAllWhitespace())
                profile.set(System.getProperty(PROFILE_PROPERTY))
    }
}
