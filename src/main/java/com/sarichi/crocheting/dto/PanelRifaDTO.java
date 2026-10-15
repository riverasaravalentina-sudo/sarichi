package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Respuesta completa del panel privado (GET /api/rifa/panel). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanelRifaDTO {
    private String rifaId;
    private LocalDateTime actualizadoEn;
    private int total;
    private long disponibles;
    private long reservados;
    private long vendidos;
    private long recaudado;
    private List<NumeroPanelDTO> numeros;
    private List<ReservaActivaDTO> reservasActivas;
}
