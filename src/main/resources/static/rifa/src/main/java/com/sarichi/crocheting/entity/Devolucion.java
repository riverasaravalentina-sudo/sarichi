package com.sarichi.crocheting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Document(collection = "devoluciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Devolucion {
    @Id
    private String id;
    
    private String pedidoId;
    
    private String usuarioId;
    
    private String motivo;
    
    @Builder.Default
    private String estado = "SOLICITADA"; // SOLICITADA, APROBADA, RECHAZADA, EN_TRANSITO, COMPLETADA
    
    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
    
    private LocalDateTime fechaAprobacion;
    
    private LocalDateTime fechaRecepcion;
    
    private String observacionesAdmin;
}
