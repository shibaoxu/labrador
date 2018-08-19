package com.labrador.authservice.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Value("${whoami}")
    private String whoami;

    @GetMapping("whoami")
    public String whoami(){
        return whoami;
    }
}
