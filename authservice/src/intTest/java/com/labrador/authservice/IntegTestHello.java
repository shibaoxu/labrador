package com.labrador.authservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegTestHello {

    @Test
    void demo(){
        System.out.println("Hello....");
        assertThat("integrationTest").isEqualToIgnoringCase("integrationTest");
    }
}
