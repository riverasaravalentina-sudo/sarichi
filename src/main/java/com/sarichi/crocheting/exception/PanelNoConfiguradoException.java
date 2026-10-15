package com.sarichi.crocheting.exception;

/**
 * Se lanza cuando se intenta usar el panel de la rifa pero
 * la variable RIFA_ADMIN_KEY no está configurada en el servidor.
 */
public class PanelNoConfiguradoException extends RuntimeException {
    public PanelNoConfiguradoException(String message) {
        super(message);
    }
}
