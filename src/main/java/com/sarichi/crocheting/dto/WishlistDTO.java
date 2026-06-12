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
public class WishlistDTO {

    private String id;
    private String productoId;
    private String productoNombre;
    private Double precioAlAgregar;
    private String categoria;
    private LocalDateTime fechaAgregado;
    private boolean notificarSiBajaPrecio;
}
