package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "articulosBlog")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloBlog {
    
    @Id
    private String id;
    
    private String titulo;
    
    @Indexed(unique = true)
    private String slug;
    
    private String contenidoHtml;
    private String resumen;
    private List<String> categorias;
    private List<String> etiquetas;
    
    private String idAutor;
    private String autorNombre;
    
    @Indexed
    private String estado; // BORRADOR, PUBLICADO, ARCHIVADO
    
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    private String metaTituloSeo;
    private String metaDescripcionSeo;
    private String imagenPrincipalUrl;
    
    @Builder.Default
    private Long visitas = 0L;
    
    private Integer tiempoLecturaMinutos;
}
