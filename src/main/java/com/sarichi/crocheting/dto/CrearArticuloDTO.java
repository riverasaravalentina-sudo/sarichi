package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearArticuloDTO {
    private String titulo;
    private String contenidoHtml;
    private String resumen;
    private List<String> categorias;
    private List<String> etiquetas;
    private String metaTituloSeo;
    private String metaDescripcionSeo;
    private String imagenPrincipalUrl;
    private Integer tiempoLecturaMinutos;
}
