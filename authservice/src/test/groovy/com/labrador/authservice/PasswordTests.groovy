package com.labrador.authservice

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(classes = AutherserviceApplication.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName('测试Oauth配置')
class PasswordTests {

    @Test
    void testCreatePassword(){
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        println encoder.encode("password")
    }

}
