package com.labrador.commontests;


import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.util.StringUtils;

public class SpringProfileActive implements ActiveProfilesResolver {
    @Override
    public String[] resolve(Class<?> aClass) {
        final String activeProfile = System.getProperty("spring.profiles.active");
        return new String[]{!StringUtils.hasLength(activeProfile) || activeProfile.equals("compose")? "test" : activeProfile};
    }
}
