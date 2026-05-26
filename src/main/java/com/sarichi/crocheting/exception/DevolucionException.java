package com.sarichi.crocheting.exception;

public class DevolucionException extends RuntimeException {
    public DevolucionException(String message) {
        super(message);
    }
    
    public DevolucionException(String message, Throwable cause) {
        super(message, cause);
    }
}
