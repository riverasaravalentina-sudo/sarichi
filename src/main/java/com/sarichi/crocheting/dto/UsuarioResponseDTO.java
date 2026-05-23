package com.sarichi.crocheting.dto;

import com.sarichi.crocheting.entity.UserRole;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioResponseDTO {

    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String fotoUrl;
    private UserRole rol;
    private String estado;
    private boolean modoOscuro;
}