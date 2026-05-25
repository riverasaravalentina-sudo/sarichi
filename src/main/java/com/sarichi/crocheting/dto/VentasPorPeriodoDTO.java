package com.sarichi.crocheting.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorPeriodoDTO {
    private LocalDate fecha;
    private Double total;
    private Long cantidadPedidos;
}
