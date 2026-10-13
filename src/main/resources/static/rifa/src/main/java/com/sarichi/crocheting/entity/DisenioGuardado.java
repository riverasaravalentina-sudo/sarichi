package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "diseniosGuardados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisenioGuardado {

    @Id
    private String id;
    private String usuarioId;
    private String productoId;
    private String productoNombre;
    private String descripcion;
    private Map<String, String> coloresSeleccionados;
    private String talla;
    private String mensajeBordado;
    private Double precioCalculado;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Double precioCotizacion;
    private String tiempoEstimado;
}
