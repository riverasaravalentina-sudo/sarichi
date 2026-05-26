package com.sarichi.crocheting.exception;

public class ProduccionException extends RuntimeException {
    public ProduccionException(String message) {
        super(message);
    }
    
    public ProduccionException(String message, Throwable cause) {
        super(message, cause);
    }
}
