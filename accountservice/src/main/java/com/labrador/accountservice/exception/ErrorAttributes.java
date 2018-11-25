package com.labrador.accountservice.exception;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ErrorAttributes {
    private ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

    @JsonIgnore
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String message;
    private String path;
    private String httpMethod;
    private Object details = null;

    public ErrorAttributes(HttpStatus status, WebRequest request){
        this.httpStatus = status;
        this.path = ((ServletWebRequest) request).getRequest().getServletPath();
        this.httpMethod = ((ServletWebRequest) request).getHttpMethod().name();
    }

    @JsonGetter("error")
    public String getStatusMessage(){
        return httpStatus.getReasonPhrase();
    }

    @JsonGetter("status")
    public int getStatusCode(){
        return this.httpStatus.value();
    }

}
