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
public class FotoProcesoDTO {
    private String id;
    private String urlFoto;
    private String descripcion;
    private LocalDateTime fechaSubida;
    private Integer orden;
}
