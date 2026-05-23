package com.sarichi.crocheting.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que extiende el perfil con datos adicionales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCompletoDTO {

    private UsuarioResponseDTO usuario;
    private Long cantidadWishlist;
    private Instant fechaRegistro;
    private Instant ultimoLogin;
}
