package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private String pedidoId;
    private Double monto;
    private String metodoPago;
    private String redirectUrl;
    private String referencia;
    private String estado;
}
