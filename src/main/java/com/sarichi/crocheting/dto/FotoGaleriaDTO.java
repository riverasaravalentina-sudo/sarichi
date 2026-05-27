package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FotoGaleriaDTO {
    private String id;
    private String idColeccion;
    private String idProducto;
    private String urlFoto;
    private String titulo;
    private String descripcion;
    private String historia;
    private Integer tiempoElaboracionHoras;
    private LocalDateTime fechaPublicacion;
    private Integer orden;
    private Long likes;
}
