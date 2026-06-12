package com.sarichi.crocheting.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalizacionDTO {

    private String id;
    private String productoId;
    private String productoNombre;
    private Map<String, String> coloresSeleccionados;
    private String talla;
    private String mensajeBordado;
    private Double precioCalculado;
    private LocalDateTime fechaCreacion;
}
