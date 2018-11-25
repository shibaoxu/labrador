package com.labrador.accountservice.entity.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Repeatable(PasswordStrength.List.class)
public @interface PasswordStrength {

    String message() default "{PasswordStrength}";
    int min() default 6;
    int max() default 20;
    int upperCase() default 1;
    int lowerCase() default 1;
    int digit() default 1;
    int special() default 1;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String value() default "";

    @Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        PasswordStrength[] value();
    }
}
