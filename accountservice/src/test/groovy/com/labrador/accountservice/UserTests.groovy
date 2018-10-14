package com.labrador.accountservice

import com.labrador.accountservice.service.UserService
import com.labrador.commontests.BaseTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserTests extends BaseTest{

    @Autowired
    private UserService userService;

    @Test
    void testGetAllUsers(){
        String test = "";

//        userService.findAll(null)
        Optional<String> name = Optional.ofNullable("Test")
        name.map(String.&toString())
    }
}
