package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private String productoId;
    private String nombreProducto;
    private Integer cantidad;
    private String colorSolicitado;
    private Double precioUnitario;
    private Double subtotal;
}
