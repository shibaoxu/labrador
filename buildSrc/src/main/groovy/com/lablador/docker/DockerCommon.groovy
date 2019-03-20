package com.lablador.docker

import org.gradle.api.provider.Property

class DockerCommon {
    final Property<String> baseImage
    final Property<String> networkId
}
