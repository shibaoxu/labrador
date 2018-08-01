package com.labrador.authservice.controller;

import com.labrador.authservice.entity.User;
import com.labrador.authservice.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/user/me")
    public User user() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return (User) userService.loadUserByUsername(username);
    }
}
