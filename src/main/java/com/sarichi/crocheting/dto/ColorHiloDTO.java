package com.sarichi.crocheting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ColorHiloDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El código hex es obligatorio")
    private String codigoHex;

    private String descripcion;

    @Builder.Default
    private Double stockMetros = 0.0;

    @Builder.Default
    private Double stockMinimo = 5.0;

    private String proveedor;

    private Double precioMetro;

    private String imagenUrl;
}