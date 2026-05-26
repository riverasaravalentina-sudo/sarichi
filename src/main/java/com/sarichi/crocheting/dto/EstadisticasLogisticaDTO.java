package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasLogisticaDTO {
    private Long despachosHoy;
    private Long despachosSemana;
    private Long pedidosPendientesDespacho;
    private Double promedioEntregaDias;
}
