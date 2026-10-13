package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "fotosGaleria")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FotoGaleria {
    
    @Id
    private String id;
    
    @Indexed
    private String idColeccion;
    
    private String idProducto; // nullable - para vincular a tienda
    
    private String urlFoto;
    private String titulo;
    private String descripcion;
    private String historia;
    
    private Integer tiempoElaboracionHoras;
    
    @Indexed
    private LocalDateTime fechaPublicacion;
    
    private Integer orden;
    
    @Builder.Default
    private Long likes = 0L;
    
    private Boolean compartidoEnInstagram;
}
