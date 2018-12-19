package com.labrador.commons.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityValidateErrorAttibutes extends ErrorAttributes {

    @JsonIgnore
    private MessageSource messageSource;

    public EntityValidateErrorAttibutes(BindException ex, WebRequest request, MessageSource messageSource){
        super(HttpStatus.BAD_REQUEST, request);
        this.messageSource = messageSource;

        BindingResult result = ex.getBindingResult();
        List<FieldErrorAttributes> fieldErrors = result.getFieldErrors().stream().map(FieldErrorAttributes::new).collect(Collectors.toList());
        List<GlobalErrorAttibutes> globalErrors = result.getGlobalErrors().stream().map(GlobalErrorAttibutes::new).collect(Collectors.toList());
        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", fieldErrors);
        details.put("objectErrors", globalErrors);
        this.setDetails(details);
        this.setMessage(messageSource.getMessage(
                "entityValidateFailure",
                new Object[]{result.getObjectName(), result.getErrorCount()},
                LocaleContextHolder.getLocale()));
    }

    @Getter
    class FieldErrorAttributes {
        private String code;
        private String message;
        private String fieldName;
        private String fieldDescription;
        public FieldErrorAttributes(FieldError fieldError) {
            this.code = fieldError.getCode();
            this.message = messageSource.getMessage(fieldError,LocaleContextHolder.getLocale());
            this.fieldName = fieldError.getField();
            this.fieldDescription = messageSource.getMessage(fieldError.getObjectName() + "." + fieldName, null, fieldName, LocaleContextHolder.getLocale());
        }

    }

    @Getter
    class GlobalErrorAttibutes {
        private String code;
        private String message;

        public GlobalErrorAttibutes(ObjectError error){
            this.code = error.getCode();
            this.message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
        }
    }

}
