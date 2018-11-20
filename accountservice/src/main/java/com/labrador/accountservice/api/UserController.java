package com.labrador.accountservice.api;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.service.UserService;
import com.labrador.accountservice.service.UserValidator;
import com.labrador.commons.entity.validation.NewEntityValidationGroup;
import com.labrador.commons.entity.validation.UpdateEntityValidationGroup;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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
    public User create(@Validated(NewEntityValidationGroup.class) User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    public Optional<User> update(@Validated(UpdateEntityValidationGroup.class) User user){
        return userService.update(user);
    }

    @PutMapping("assignToRoles")
    public void assignToRoles(@RequestParam("userId") @NonNull String userId, @RequestParam("roleId") @NonNull String... rolesIds){
        userService.assignToRole(userId, rolesIds);
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
