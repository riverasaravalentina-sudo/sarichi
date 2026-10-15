package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/** Número del tablero con datos de comprador (solo panel privado). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumeroPanelDTO {
    private Integer numero;
    private String estado;
    private String compradorNombre;
    private String compradorTelefono;
    private String codigoReserva;
    private LocalDateTime reservadoHasta;
    private String notas;
}
