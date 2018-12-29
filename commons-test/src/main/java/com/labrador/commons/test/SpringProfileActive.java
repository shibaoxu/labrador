package com.labrador.commons.test;


import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.StringUtils;

public class SpringProfileActive implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> aClass) {
        final String jvmProfile = System.getProperty("spring.profiles.active");
        final String envProfile = System.getenv("SPRING_PROFILES.ACTIVE");
        final String defaultProfile = "test-h2";

        if (StringUtils.hasText(jvmProfile)) return new String[]{jvmProfile};
        if (StringUtils.hasText(envProfile)) return new String[]{envProfile};
        return new String[]{defaultProfile};
    }
}
