package com.sarichi.crocheting.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoFiltroDTO {

    private String categoria;
    private String colorId;
    private Double precioMin;
    private Double precioMax;
    private String busqueda;

    @Builder.Default
    private int pagina = 0;

    @Builder.Default
    private int tamano = 12;
}