package com.labrador.accountservice.api;

import com.labrador.accountservice.exception.EntityNotFoundErrorAttributes;
import com.labrador.accountservice.exception.EntityNotFoundException;
import com.labrador.accountservice.exception.EntityValidateErrorAttibutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        EntityValidateErrorAttibutes errorAttributes = new EntityValidateErrorAttibutes(ex, request, messageSource);
        return new ResponseEntity<>(errorAttributes, headers, errorAttributes.getHttpStatus());
    }

    @ExceptionHandler({EntityNotFoundException.class, Exception.class})
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex,  WebRequest request){
        return new ResponseEntity<>(new EntityNotFoundErrorAttributes(ex, request, messageSource), null, HttpStatus.BAD_REQUEST);
    }
}
