package com.labrador.accountservice.entity.validation;

import org.passay.*;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    private int min;
    private int max;
    private int upperCase;
    private int lowerCase;
    private int degit;
    private int special;
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (StringUtils.hasText(password)){
            PasswordValidator validator = new PasswordValidator(Arrays.asList(
                    new LengthRule(min, max),
                    new CharacterRule(EnglishCharacterData.UpperCase, this.upperCase),
                    new CharacterRule(EnglishCharacterData.LowerCase, this.lowerCase),
                    new CharacterRule(EnglishCharacterData.Digit, this.degit),
                    new CharacterRule(EnglishCharacterData.Special, this.special),
//                    new UsernameRule(false, true),
                    new WhitespaceRule()
            ));
            return validator.validate(new PasswordData(password)).isValid();
        }
        return false;
    }

    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.degit = constraintAnnotation.digit();
        this.upperCase = constraintAnnotation.upperCase();
        this.lowerCase = constraintAnnotation.lowerCase();
        this.special = constraintAnnotation.special();
    }
}
