package com.sarichi.crocheting.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeDTO {

    private String id;
    private String pedidoId;
    private String remitenteId;
    private String remitenteNombre;
    private String remitenteRol;
    private String contenido;
    private LocalDateTime timestamp;
    private boolean leido;
}
