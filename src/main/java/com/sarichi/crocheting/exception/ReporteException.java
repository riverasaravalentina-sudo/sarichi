package com.sarichi.crocheting.exception;

public class ReporteException extends RuntimeException {
    public ReporteException(String message) {
        super(message);
    }
    
    public ReporteException(String message, Throwable cause) {
        super(message, cause);
    }
}
