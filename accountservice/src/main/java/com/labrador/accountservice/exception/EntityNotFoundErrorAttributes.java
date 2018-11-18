package com.labrador.accountservice.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;

public class EntityNotFoundErrorAttributes extends ErrorAttributes {
    private String messgeCode = "EntityNotFound.message";
    private MessageSource messageSource;
    public EntityNotFoundErrorAttributes(EntityNotFoundException ex, WebRequest request, MessageSource messageSource){
        super(HttpStatus.BAD_REQUEST, request);
        this.messageSource = messageSource;
//        setMessage(this.messageSource.getMessage());

    }
}
