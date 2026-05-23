package com.sarichi.crocheting.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String tipo;
    private long expiresIn;
    private UsuarioResponseDTO usuario;
}