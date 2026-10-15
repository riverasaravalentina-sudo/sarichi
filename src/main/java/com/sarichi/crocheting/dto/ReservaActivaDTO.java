package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Reserva activa agrupada por código (solo panel privado). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaActivaDTO {
    private String codigoReserva;
    private List<Integer> numeros;
    private LocalDateTime reservadoHasta;
    private String compradorNombre;
    private String compradorTelefono;
    private int total;
}
