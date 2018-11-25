package com.labrador.accountservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class ResourceNotFoundErrorAttributes extends ErrorAttributes {
    @JsonIgnore
    private String messgeCode = "ResourceNotFound.message";
    @JsonIgnore
    private String detailCode = "ResourceNotFound.details";

    public ResourceNotFoundErrorAttributes(ResourceNotFoundException ex, WebRequest request, MessageSource messageSource) {
        super(HttpStatus.NOT_FOUND, request);
        this.setMessage(messageSource.getMessage(messgeCode,null, "resource not found ", LocaleContextHolder.getLocale()));
        this.setDetails(
                messageSource.getMessage(
                        detailCode,
                        new Object[]{ex.getResourceClassName(), ex.getResourceId()},
                        String.format("resource %s with criteria:%s not found", ex.getResourceClassName(), ex.getResourceId()),
                        LocaleContextHolder.getLocale()
                )
        );
    }
}
