package com.sarichi.crocheting.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PerfilCompletoDTO {
    private UsuarioResponseDTO usuario;
    private Long cantidadWishlist;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoLogin;
}