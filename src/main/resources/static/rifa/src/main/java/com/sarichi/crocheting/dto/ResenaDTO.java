package com.sarichi.crocheting.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaDTO {
    private String id;
    private String pedidoId;
    private String productoId;
    private String productoNombre;
    private String usuarioId;
    private String usuarioNombre;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}
