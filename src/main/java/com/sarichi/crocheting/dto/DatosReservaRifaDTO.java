package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de la persona que apartó, registrados posteriormente sobre una
 * reserva ya creada (POST /api/rifa/reservar/{codigo}/datos).
 * Todos los campos son opcionales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosReservaRifaDTO {

    private String compradorNombre;
    private String compradorTelefono;
    private String notas;
}
