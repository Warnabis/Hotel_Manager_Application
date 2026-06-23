package com.warnabis.Hotel_SpringBoot_Application.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s с id=%s не найден", resourceName, id));
    }
}
