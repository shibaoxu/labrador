package com.labrador.commons.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class BusinessErrorAttributes extends ErrorAttributes {
    public BusinessErrorAttributes(BusinessException ex, WebRequest request, MessageSource messageSource) {
        super(HttpStatus.CONFLICT, request);
        setMessage(messageSource.getMessage(
                ex.getMessageCode(),
                ex.getArguments(),
                ex.getDefaultMessage(),
                LocaleContextHolder.getLocale()
        ));
        setDetails(ex.getDetails());
    }
}
