package com.sarichi.crocheting.exception;

public class BlogException extends RuntimeException {
    public BlogException(String message) {
        super(message);
    }
    
    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }
}
