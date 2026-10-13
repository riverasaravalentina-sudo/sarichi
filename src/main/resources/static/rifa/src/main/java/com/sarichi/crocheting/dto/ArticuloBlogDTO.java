package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloBlogDTO {
    private String id;
    private String titulo;
    private String slug;
    private String contenidoHtml;
    private String resumen;
    private List<String> categorias;
    private List<String> etiquetas;
    private String autorNombre;
    private String estado;
    private LocalDateTime fechaPublicacion;
    private Long visitas;
    private Integer tiempoLectura;
    private String imagenPrincipalUrl;
}
