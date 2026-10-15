package com.sarichi.crocheting.exception;

import java.util.List;

/**
 * Se lanza cuando se intenta reservar o vender un número
 * que ya no está disponible (RESERVADO o VENDIDO).
 */
public class ConflictoReservaException extends RuntimeException {

    private final List<Integer> numerosConflicto;

    public ConflictoReservaException(String message, List<Integer> numerosConflicto) {
        super(message);
        this.numerosConflicto = numerosConflicto;
    }

    public List<Integer> getNumerosConflicto() {
        return numerosConflicto;
    }
}
