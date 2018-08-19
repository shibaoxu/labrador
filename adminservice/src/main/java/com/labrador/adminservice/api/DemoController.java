package com.labrador.adminservice.api;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hello")
    public String hello(){
        return "Welcome";
    }

    @GetMapping("whoAmI")
    @HystrixCommand(fallbackMethod = "busy")
    public String whoAmI(){
        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("AUTHSERVICE", false);
        return instanceInfo.getHomePageUrl();
    }

    public String busy(){
        return "I am busy, please wait.";
    }

    @GetMapping("comsumeWhoAmI")
    public String comsumeWhoAmI(){
        String name = restTemplate.getForEntity("http://AUTHSERVICE/api/demo/whoami", String.class).getBody();
        return "Welcome " + name;
    }
}
