package com.sarichi.crocheting.entity;

/**
 * Estados posibles de un número de la rifa.
 *
 * DISPONIBLE → libre, cualquier visitante puede elegirlo.
 * RESERVADO  → apartado por un cliente mientras paga (tiene fecha límite).
 * VENDIDO    → pago confirmado por la artesana; ya no se puede elegir.
 */
public enum EstadoNumero {
    DISPONIBLE,
    RESERVADO,
    VENDIDO
}
