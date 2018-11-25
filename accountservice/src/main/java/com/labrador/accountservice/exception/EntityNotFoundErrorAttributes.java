package com.labrador.accountservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;


public class EntityNotFoundErrorAttributes extends ErrorAttributes {
    @JsonIgnore
    private String messgeCode = "EntityNotFound.message";
    @JsonIgnore
    private String detailCode = "EntityNotFound.details";
    public EntityNotFoundErrorAttributes(EntityNotFoundException ex, WebRequest request, MessageSource messageSource){
        super(HttpStatus.BAD_REQUEST, request);
        this.setMessage(messageSource.getMessage(messgeCode,null, "unable get entity ", LocaleContextHolder.getLocale()));
        this.setDetails(
                messageSource.getMessage(
                        detailCode,
                        new Object[]{ex.getEntityClassName(), ex.getEntityId()},
                        String.format("unable get %s with id %s", ex.getEntityClassName(), ex.getEntityId()),
                        LocaleContextHolder.getLocale()
                )
        );
    }
}
