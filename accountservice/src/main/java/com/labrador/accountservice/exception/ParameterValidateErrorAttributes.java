package com.labrador.accountservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ParameterValidateErrorAttributes extends ErrorAttributes {
    @JsonIgnore
    private final String messageCode = "ParameterValidateFailure.message";
    @JsonIgnore
    private MessageSource messageSource;
    public ParameterValidateErrorAttributes(ConstraintViolationException ex, WebRequest request, MessageSource messageSource) {
        super(HttpStatus.BAD_REQUEST, request);
        this.messageSource = messageSource;
        Set<ConstraintViolation<?>> violations =  ex.getConstraintViolations();
        setMessage(
                messageSource.getMessage(
                    messageCode, null, "request parameter validate failue", LocaleContextHolder.getLocale()
                )
        );
        setDetails(violations.stream().map(ParameterError::new).collect(Collectors.toList()));

    }

    @Getter
    class ParameterError {
        @JsonIgnore
        private final List<String> internalAnnotationAttributes = Arrays.asList("message", "groups", "payload");

        private String className;
        private String methodName;
        private String paramName;
        private String messageCode;
        private String message;

        public ParameterError(ConstraintViolation<?> violation){
           this.className = violation.getRootBean().getClass().getName();
           String[] propertyPath = violation.getPropertyPath().toString().split("\\.");
           this.methodName = propertyPath[0];
           this.paramName = propertyPath[1];
           this.messageCode = parseMessageCode(violation.getMessageTemplate());
           this.message = messageSource.getMessage(
                   messageCode,
                   parseArguments(violation.getConstraintDescriptor()),
                   "parameter validate failure",
                   LocaleContextHolder.getLocale()
           );
        }

        private String parseMessageCode(String messageTemplate){
            Matcher matcher = Pattern.compile("\\w*[A-Z]\\w*").matcher(messageTemplate);
            if (matcher.find()){
                return matcher.group();
            }else{
                log.warn("can not parse the message code.");
                return "";
            }
        }

        private Object[] parseArguments(ConstraintDescriptor<?> descriptor){
            List<Object> arguments = new ArrayList<>();
            arguments.add(className);
            Map<String, Object> attributesToExpose = new TreeMap<>();
            descriptor.getAttributes().forEach((attributeName, attributeValue) -> {
                if (!internalAnnotationAttributes.contains(attributeName)) {
                    attributesToExpose.put(attributeName, attributeValue.toString());
                }
            });
            arguments.addAll(attributesToExpose.values());
            return arguments.toArray();

        }
    }
}
