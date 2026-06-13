package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "movimientosBodega")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoBodega {

    @Id
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
