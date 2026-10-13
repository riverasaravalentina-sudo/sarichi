package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetoMensualDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private String patronUrl;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private String premioDescripcion;
    private String ganadorId;
    private Long participacionesCount;
}
