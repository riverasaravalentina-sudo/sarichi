package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String correo;

    private String passwordHash;

    private String telefono;

    private String fotoUrl;

    @Builder.Default
    private UserRole rol = UserRole.CLIENTE;

    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Builder.Default
    private String estado = "ACTIVO";

    private String googleId;

    private String refreshToken;

    private String tokenRecuperacion;

    private LocalDateTime tokenRecuperacionExpira;

    @Builder.Default
    private List<String> direcciones = new ArrayList<>();

    @Builder.Default
    private LocalDateTime ultimoLogin = LocalDateTime.now();

    @Builder.Default
    private boolean modoOscuro = false;
}