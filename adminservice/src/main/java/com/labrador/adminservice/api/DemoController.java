package com.labrador.adminservice.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @GetMapping
    public String hello(){
        return "hello, i am adminservice";
    }
//    @Autowired
//    private EurekaClient discoveryClient;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @GetMapping("/hello")
//    public String hello(){
//        return "Welcome";
//    }
//
//    @GetMapping("whoAmI")
//    @HystrixCommand(fallbackMethod = "busy")
//    public String whoAmI(){
//        InstanceInfo instanceInfo = discoveryClient.getNextServerFromEureka("AUTHSERVICE", false);
//        return instanceInfo.getHomePageUrl();
//    }
//
//    public String busy(){
//        return "I am busy, please wait.";
//    }
//
//    @GetMapping("comsumeWhoAmI")
//    public String comsumeWhoAmI(){
//        String name = restTemplate.getForEntity("http://AUTHSERVICE/api/demo/whoami", String.class).getBody();
//        return "Welcome " + name;
//    }
}
