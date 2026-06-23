package com.warnabis.Hotel_SpringBoot_Application.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
