package com.labrador.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private String messageCode;
    private Object details;
    private String[] arguments;
    private String defaultMessage;

    public BusinessException(String messageCode){
        this.messageCode = messageCode;
    }
}
