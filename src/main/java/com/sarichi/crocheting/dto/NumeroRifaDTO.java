package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Número del tablero con su estado (sin datos personales). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumeroRifaDTO {
    private Integer numero;
    private String estado;
}
