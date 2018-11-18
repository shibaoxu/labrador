package com.labrador.accountservice.exception;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    @NonNull
    private String entityClassName;
    @NonNull
    private String entityId;
}
