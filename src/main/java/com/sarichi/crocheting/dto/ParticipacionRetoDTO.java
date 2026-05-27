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
public class ParticipacionRetoDTO {
    private String id;
    private String idReto;
    private String idUsuario;
    private String nombreUsuario;
    private String urlFotoParticipacion;
    private String descripcion;
    private LocalDateTime fechaParticipacion;
    private Long votos;
    private String estado;
}
