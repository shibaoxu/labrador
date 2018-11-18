package com.labrador.commons.entity.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BlankValidator implements ConstraintValidator<Blank, String> {
    @Override
    public void initialize(Blank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value);
    }
}
