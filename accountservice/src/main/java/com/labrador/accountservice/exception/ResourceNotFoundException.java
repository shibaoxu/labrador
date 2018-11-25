package com.labrador.accountservice.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    @NonNull
    private String resourceClassName;
    @NonNull
    private String resourceId;
}
