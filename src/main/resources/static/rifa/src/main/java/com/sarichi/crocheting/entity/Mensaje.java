package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "mensajesPedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {

    @Id
    private String id;
    private String pedidoId;
    private String remitenteId;
    private String remitenteNombre;
    private String remitenteRol;
    private String contenido;
    private LocalDateTime timestamp;
    private boolean leido;
}
