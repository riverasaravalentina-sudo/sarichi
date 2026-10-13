package com.sarichi.crocheting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FotoProceso {
    private String id;
    
    private String urlFoto;
    
    private String descripcion;
    
    @Builder.Default
    private LocalDateTime fechaSubida = LocalDateTime.now();
    
    private Integer orden; // para ordenar las fotos en la galería
}
