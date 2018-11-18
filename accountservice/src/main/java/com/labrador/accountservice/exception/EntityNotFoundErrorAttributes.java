package com.labrador.accountservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;


public class EntityNotFoundErrorAttributes extends ErrorAttributes {
    private String messgeCode = "EntityNotFound.message";
    private String detailCode = "EntityNotFound.details";
    private MessageSource messageSource;
    public EntityNotFoundErrorAttributes(EntityNotFoundException ex, WebRequest request, MessageSource messageSource){
        super(HttpStatus.BAD_REQUEST, request);
        this.messageSource = messageSource;
        this.setMessage(this.messageSource.getMessage(messgeCode,null, "unable find entity ", LocaleContextHolder.getLocale()));
        this.setDetails(
                this.messageSource.getMessage(
                        detailCode,
                        new Object[]{ex.getEntityClassName(), ex.getEntityId()},
                        "unable find " + ex.getEntityClassName() + "with id " + ex.getEntityId(),
                        LocaleContextHolder.getLocale()
                )
        );
    }
}
