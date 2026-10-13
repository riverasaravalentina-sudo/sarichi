package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColeccionGaleriaDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private String slug;
    private String portadaUrl;
    private Integer orden;
    private String estado;
    private List<FotoGaleriaDTO> fotos;
}
