package com.sarichi.crocheting.exception;

public class DespachoException extends RuntimeException {
    public DespachoException(String message) {
        super(message);
    }
    
    public DespachoException(String message, Throwable cause) {
        super(message, cause);
    }
}
