package com.sarichi.crocheting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "despachos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {
    @Id
    private String id;
    
    private String pedidoId;
    
    @Builder.Default
    private LocalDateTime fechaDespacho = LocalDateTime.now();
    
    private String transportadora; // ej: "Interrapidísimo", "Coordinadora"
    
    private String numeroGuia;
    
    @Builder.Default
    private String estado = "PENDIENTE"; // PENDIENTE, EN_TRANSITO, ENTREGADO, DEVUELTO
    
    private LocalDateTime fechaEstimadaEntrega;
    
    private String observaciones;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
