package com.labrador.accountservice.api;

import com.labrador.accountservice.entity.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public String index(){
        return "welcome account service";
    }

    @PostMapping
    public String add(@Valid User user, BindingResult result) {
        System.out.println("add new user");
        return "============";
    }
}
