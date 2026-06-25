package com.warnabis.hotel_springboot_application.exception;

public class SecurityConfigurationException extends RuntimeException {
    public SecurityConfigurationException(String message) {
        super(message);
    }

    public SecurityConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}