package com.warnabis.Hotel_SpringBoot_Application.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
