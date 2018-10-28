package com.labrador.accountservice;

import com.labrador.commontests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageTest extends BaseTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    void testSayHello(){
        String message = messageSource.getMessage("user.password",new Object[]{}, Locale.CHINA);
        System.out.println(message);
    }
}
