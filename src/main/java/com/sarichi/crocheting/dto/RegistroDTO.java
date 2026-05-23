package com.sarichi.crocheting.dto;

import com.sarichi.crocheting.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String contrasena;

    @NotBlank(message = "Confirma la contraseña")
    private String confirmarContrasena;

    private String telefono;

    private UserRole rol;
}