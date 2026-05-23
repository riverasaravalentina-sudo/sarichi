package com.sarichi.crocheting.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precioBase;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private List<String> coloresDisponibles = new ArrayList<>();

    @Builder.Default
    private List<String> fotosUrls = new ArrayList<>();

    private Integer tiempoElaboracionDias;

    private String estado;
}