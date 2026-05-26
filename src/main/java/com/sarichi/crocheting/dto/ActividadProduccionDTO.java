package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadProduccionDTO {
    private String id;
    private String pedidoId;
    private String pedidoNumero;
    private String productoId;
    private String productoNombre;
    private String artesanaId;
    private String artesanaNombre;
    private Integer cantidad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private String estado;
    private String notas;
    private List<FotoProcesoDTO> fotos;
}
