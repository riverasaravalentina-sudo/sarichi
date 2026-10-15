package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Respuesta pública del tablero (GET /api/rifa/numeros). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableroRifaDTO {
    private String rifaId;
    private LocalDateTime actualizadoEn;
    private int total;
    private long disponibles;
    private long reservados;
    private long vendidos;
    private List<NumeroRifaDTO> numeros;
}
