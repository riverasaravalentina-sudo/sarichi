package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    private String id;

    private String usuarioId;

    @Builder.Default
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();

    private Double total;

    @Builder.Default
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PRODUCCION, LISTO, DESPACHADO, ENTREGADO, CANCELADO

    private String direccionEnvio;

    private LocalDateTime fechaEntregaEstimada;

    private String notaProductor;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
