package com.sarichi.crocheting.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private String id;
    private String usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaPedido;
    private List<ItemPedidoDTO> items;
    private Double total;
    private String estado;
    private String direccionEnvio;
}
