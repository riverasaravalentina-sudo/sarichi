package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    private String id;

    private String nombre;

    private String descripcion;

    private Double precioBase;

    /** Amigurumis | Accesorios | Ropa | Hogar */
    private String categoria;

    @Builder.Default
    private Integer stock = 0;

    /** Referencias a IDs de ColorHilo */
    @Builder.Default
    private List<String> coloresDisponibles = new ArrayList<>();

    @Builder.Default
    private List<String> fotosUrls = new ArrayList<>();

    private Integer tiempoElaboracionDias;

    /** ACTIVO | INACTIVO */
    @Builder.Default
    private String estado = "ACTIVO";

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}