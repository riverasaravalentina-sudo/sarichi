package com.sarichi.crocheting.exception;

/**
 * Se lanza cuando la clave del panel de la rifa (RIFA_ADMIN_KEY)
 * es incorrecta o no fue enviada.
 */
public class ClaveInvalidaException extends RuntimeException {
    public ClaveInvalidaException(String message) {
        super(message);
    }
}
