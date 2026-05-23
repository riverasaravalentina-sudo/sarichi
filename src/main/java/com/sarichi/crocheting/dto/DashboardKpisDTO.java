package com.sarichi.crocheting.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardKpisDTO {

    // Ventas
    private Double ventasHoy;
    private Double ventasAyer;
    private Double variacionVentas;

    // Pedidos
    private Long pedidosPendientes;
    private Long pedidosEnProduccion;

    // Logística
    private Long productosParaDespachar;

    // Bodega
    private Long stockCritico;

    // Extra
    private Long totalClientes;
    private Long totalProductos;
}