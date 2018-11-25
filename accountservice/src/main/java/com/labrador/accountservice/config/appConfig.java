package com.labrador.accountservice.config;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class appConfig {

    @Bean
    public PlatformResourceBundleLocator platformResourceBundleLocator(){
        return new PlatformResourceBundleLocator("messages");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor() {
//        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
//        processor.setValidator(validator());
//        return processor;
//    }
//
//    @Bean
//    public Validator validator() {
//        return Validation
//                .byProvider(HibernateValidator.class)
//                .configure()
//                //快速返回模式，有一个验证失败立即返回错误信息
////                .failFast(true)
//                .buildValidatorFactory()
//                .getValidator();
//    }
}
