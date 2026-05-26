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
public class DespachoDTO {
    private String id;
    private String pedidoId;
    private String pedidoNumero;
    private String clienteNombre;
    private String ciudadDestino;
    private LocalDateTime fechaDespacho;
    private String transportadora;
    private String numeroGuia;
    private String estado;
    private LocalDateTime fechaEstimadaEntrega;
    private String observaciones;
}
