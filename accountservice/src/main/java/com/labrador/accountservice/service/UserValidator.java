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
//    @Autowired
//    private LocalValidatorFactoryBean defaultValidator;
//    private Validator defaultValidator;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
//        defaultValidator.validate(target, errors);

        User user = (User)target;
        validateUnique(user, errors);
        if (user.isNew()){
            validatePassword(user, errors);
        }
    }

    public void validateUnique(User user, Errors errors){
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "user.username.unique", new Object[]{user.getUsername()},"");
        }
    }

    public void validatePassword(User user, Errors errors){
        boolean isValidate = false;
        if (StringUtils.hasText(user.getDisplayName())) {
            PasswordValidator validator = new PasswordValidator(Arrays.asList(
                    new LengthRule(6, 20),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1),
                    new UsernameRule(false, true),
                    new WhitespaceRule()

            ));
            isValidate = validator.validate(new PasswordData(user.getUsername(), user.getPlainPassword())).isValid();
        }

        if (!isValidate) {
            errors.rejectValue("password", "user.password.strength", "The password is too weak.");
        }

    }
}
