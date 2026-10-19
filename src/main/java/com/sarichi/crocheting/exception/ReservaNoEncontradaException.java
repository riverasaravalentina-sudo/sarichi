package com.sarichi.crocheting.exception;

/**
 * Se lanza cuando el código de reserva no corresponde a una reserva
 * activa (no existe, ya venció o fue convertida en venta).
 */
public class ReservaNoEncontradaException extends RuntimeException {

    public ReservaNoEncontradaException(String message) {
        super(message);
    }
}
