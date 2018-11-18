package com.labrador.accountservice.service;

import com.labrador.accountservice.entity.User;
import com.labrador.accountservice.repository.UserRepository;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

@Service
@Transactional(readOnly = true)
public class UserValidator implements Validator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User)target;
        validateUnique(user, errors);
    }

    public void validateUnique(User user, Errors errors){
        if (user.isNew()) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                errors.rejectValue("username", "user.username.unique", new Object[]{user.getUsername()},"");
            }
        }
    }
}
