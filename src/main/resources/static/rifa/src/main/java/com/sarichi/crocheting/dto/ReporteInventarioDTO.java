package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteInventarioDTO {
    private Long productosActivos;
    private Long productosAgotados;
    private Long stockCritico;
    private Double valorTotalInventario;
    private List<HiloAgotarseDTO> hilosPorAgotarse;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HiloAgotarseDTO {
        private String hiloId;
        private String nombre;
        private Double stockMetros;
        private Double stockMinimo;
    }
}
