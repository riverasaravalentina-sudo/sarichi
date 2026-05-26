package com.sarichi.crocheting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "actividades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadProduccion {
    @Id
    private String id;
    
    private String pedidoId;
    
    private String productoId;
    
    private String artesanaId;
    
    private Integer cantidad;
    
    private LocalDateTime fechaInicio;
    
    private LocalDateTime fechaFinalizacion;
    
    @Builder.Default
    private String estado = "PENDIENTE"; // PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
    
    private String notas;
    
    private List<FotoProceso> fotos;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
