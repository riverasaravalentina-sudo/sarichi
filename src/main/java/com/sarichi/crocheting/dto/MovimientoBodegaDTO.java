package com.sarichi.crocheting.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoBodegaDTO {

    private String id;
    private LocalDateTime fecha;
    private String tipo;
    private String itemId;
    private String itemNombre;
    private String itemTipo;
    private Double cantidad;
    private Double stockAnterior;
    private Double stockPosterior;
    private String responsable;
    private String observacion;
}
