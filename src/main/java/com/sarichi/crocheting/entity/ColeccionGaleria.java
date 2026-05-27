package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "coleccionesGaleria")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColeccionGaleria {
    
    @Id
    private String id;
    
    private String nombre;
    private String descripcion;
    
    @Indexed(unique = true)
    private String slug;
    
    private Integer orden;
    private String portadaUrl;
    
    @Indexed
    private String estado; // ACTIVA, INACTIVA
}
