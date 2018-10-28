package com.labrador.accountservice.api;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.service.UserService;
import com.labrador.accountservice.service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping
    public String index(){
        return "welcome account service";
    }

    @PostMapping
    public String add(@Valid User user) {
        System.out.println("add new user");
        return "============";
    }

    @GetMapping("{id}")
    public Optional<User> get(@PathVariable String id) {
        return userService.find(id);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.addValidators(userValidator);
    }
}
