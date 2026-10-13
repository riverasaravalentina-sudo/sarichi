package com.sarichi.crocheting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RestablecerContrasenaDTO {

    @NotBlank(message = "El token no puede estar vacío")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String nuevaContrasena;

    @NotBlank(message = "Confirma la contraseña")
    private String confirmarContrasena;
}