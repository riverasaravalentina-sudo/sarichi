package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    private String id;

    private String pedidoId;
    private String usuarioId;
    private Double monto;
    private String moneda;
    private String metodoPago;
    private String referencia;
    private String estado;
    private String proveedor;
    private String redirectUrl;
    private String transaccionId;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaActualizacion;
}
