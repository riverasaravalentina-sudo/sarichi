package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "coloresHilo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorHilo {

    @Id
    private String id;

    private String nombre;

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