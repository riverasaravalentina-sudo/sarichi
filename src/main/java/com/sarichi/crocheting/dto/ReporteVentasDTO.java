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
public class ReporteVentasDTO {
    private LocalDateTime periodo;
    private Double totalVentas;
    private Long cantidadPedidos;
    private Double promedioPedido;
    private List<VentasPorCategoriaDTO> ventasPorCategoria;
    private List<TopProductoDTO> topProductos;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentasPorCategoriaDTO {
        private String categoria;
        private Double total;
        private Long cantidad;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductoDTO {
        private String productoId;
        private String nombre;
        private Long cantidadVendida;
        private Double ingresos;
    }
}
