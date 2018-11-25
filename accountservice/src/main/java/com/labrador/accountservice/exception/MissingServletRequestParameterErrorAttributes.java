package com.labrador.accountservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

public class MissingServletRequestParameterErrorAttributes extends ErrorAttributes {
    @JsonIgnore
    private final String messgeCode = "MissingRequestParameter.message";
    @JsonIgnore
    private final String detailsCode = "MissingRequestParameter.details";
    public MissingServletRequestParameterErrorAttributes(MissingServletRequestParameterException ex, WebRequest request, MessageSource messageSource) {
        super(HttpStatus.BAD_REQUEST, request);
        this.setMessage(
                messageSource.getMessage(
                        messgeCode,
                        null,
                        "Required parameter is not present",
                        LocaleContextHolder.getLocale()
                        )
        );
        this.setDetails(
                messageSource.getMessage(
                        detailsCode,
                        new Object[]{ex.getParameterType(), ex.getParameterName()},
                        "Required parameter is not present",
                        LocaleContextHolder.getLocale()
                )
        );
    }
}
