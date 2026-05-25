package com.sarichi.crocheting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    private String productoId;

    private String nombreProducto;

    private Integer cantidad;

    private String colorSolicitado;

    private Double precioUnitario;

    private Double subtotal;
}
