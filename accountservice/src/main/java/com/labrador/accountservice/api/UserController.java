package com.labrador.accountservice.api;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.entity.validation.PasswordStrength;
import com.labrador.accountservice.service.UserService;
import com.labrador.accountservice.service.UserValidator;
import com.labrador.commons.entity.validation.NewEntityValidationGroup;
import com.labrador.commons.entity.validation.UpdateEntityValidationGroup;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @PostMapping
    public User create(@Validated(NewEntityValidationGroup.class) User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    public Optional<User> update(@Validated(UpdateEntityValidationGroup.class) User user) {
        return userService.update(user);
    }

    @PutMapping("assignToRoles")
    public void assignToRoles(
                    @RequestParam("userId")
                    @NotBlank
                    String userId,
                    @RequestParam("roleId")
                    @Size(max = 100)
                    String... rolesIds) {
        userService.assignToRoles(userId, rolesIds);
    }

    @PutMapping("removeFromRoles")
    public void removeFromRoles(
            @RequestParam("userId")
            @NotBlank
            String userId,
            @RequestParam("roleId")
            @Size
            String... roleId){
        userService.removeFromRoles(userId, roleId);
    }
    @GetMapping("{id}")
    public User get(@PathVariable("id") String id) {
        return userService.get(id);
    }

    @GetMapping(params = {"username"})
    public User getByUsername(@RequestParam("username") @NotBlank String username){
        return userService.getByUsername(username);
    }

    @GetMapping(params = {"!username"})
    public Page<User> findAll(@NonNull Pageable pageable){
        return userService.findAll(pageable);
    }

    @GetMapping
    public Page<User> findAll(@RequestParam("criteria") String criteria, @NonNull Pageable pageable){
        return userService.findAll(criteria, pageable);
    }

    @PostMapping("{id}/password")
    public void changePassword(
            @PathVariable("id")
            @NotBlank
            String userId,
            @PasswordStrength
            @RequestParam("oldPassword")
            @NotBlank
            String oldPassword,
            @PasswordStrength
            @RequestParam("newPassword")
            @NotBlank
            String newPassword){
        userService.changePassword(userId, oldPassword, newPassword);
    }

    @PutMapping("{id}/enable")
    public void enable(@PathVariable("id") @NotBlank String userId){
        userService.disableOrEnable(userId, true);
    }
    @PutMapping("{id}/disable")
    public void disable(@PathVariable("id") @NotBlank String userId){
        userService.disableOrEnable(userId, false);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") @NotBlank String userId){
        userService.delete(userId);
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userValidator);
    }
}
