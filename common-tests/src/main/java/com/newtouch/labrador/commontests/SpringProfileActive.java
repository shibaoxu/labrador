package com.newtouch.labrador.commontests;


import org.springframework.test.context.ActiveProfilesResolver;

public class SpringProfileActive implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> aClass) {
        final String activeProfile = System.getProperty("spring.profiles.active");
        return new String[]{activeProfile == null ? "test" : activeProfile};
    }
}
