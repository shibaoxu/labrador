package com.labrador.commons.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;


@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        EntityValidateErrorAttibutes errorAttributes = new EntityValidateErrorAttibutes(ex, request, messageSource);
        return new ResponseEntity<>(errorAttributes, headers, errorAttributes.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new MissingServletRequestParameterErrorAttributes(ex, request, messageSource),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, Exception.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex,  WebRequest request){
        return new ResponseEntity<>(new EntityNotFoundErrorAttributes(ex, request, messageSource), null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        return new ResponseEntity<>(new ParameterValidateErrorAttributes(ex, request, messageSource), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request){
        return new ResponseEntity<>(new BusinessErrorAttributes(ex, request, messageSource), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        return new ResponseEntity<>(new ResourceNotFoundErrorAttributes(ex, request, messageSource), HttpStatus.NOT_FOUND);
    }
}
