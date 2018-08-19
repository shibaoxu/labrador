package com.labrador.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("newtouch.com");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http
//                .antMatcher("/user/me").authorizeRequests().anyRequest().authenticated()
//                .and()
//                .antMatcher("/api/demo/**").authorizeRequests().anyRequest().permitAll();
//        http.antMatcher("/api/demo/**").authorizeRequests().anyRequest().permitAll();

        http.requestMatchers().antMatchers("/user/me", "/api/demo/**")
                .and()
                .authorizeRequests().antMatchers("/user/me").authenticated()
                .and()
                .authorizeRequests().antMatchers("/api/demo/**").permitAll();
    }
}
