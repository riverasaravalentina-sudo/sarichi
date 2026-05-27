package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "retosMensuales")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetoMensual {
    
    @Id
    private String id;
    
    private String nombre;
    private String descripcion;
    private String patronUrl; // URL a PDF o imagen del patrón
    
    @Indexed
    private LocalDateTime fechaInicio;
    
    @Indexed
    private LocalDateTime fechaFin;
    
    @Indexed
    private String estado; // ACTIVO, FINALIZADO, CANCELADO
    
    private String premioDescripcion;
    private String ganadorId; // nullable - ID del usuario ganador
    
    private LocalDateTime fechaCreacion;
}
