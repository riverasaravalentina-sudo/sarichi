package com.sarichi.crocheting.exception;

public class RetoException extends RuntimeException {
    public RetoException(String message) {
        super(message);
    }
    
    public RetoException(String message, Throwable cause) {
        super(message, cause);
    }
}
