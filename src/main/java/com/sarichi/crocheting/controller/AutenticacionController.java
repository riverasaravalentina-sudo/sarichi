package com.sarichi.crocheting.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.dto.LoginDTO;
import com.sarichi.crocheting.dto.RecuperarContrasenaDTO;
import com.sarichi.crocheting.dto.RegistroDTO;
import com.sarichi.crocheting.dto.RestablecerContrasenaDTO;
import com.sarichi.crocheting.dto.TokenResponseDTO;
import com.sarichi.crocheting.dto.UsuarioResponseDTO;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.service.AutenticacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación",
     description = "Registro, login, refresh, logout y recuperación de contraseña")
public class AutenticacionController {

    @Autowired
    private AutenticacionService autenticacionService;

    /**
     * RF-AUTH-01 — Registro
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario",
               description = "Crea cuenta con nombre, correo, contraseña y rol.")
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody RegistroDTO registroDTO) {
        UsuarioResponseDTO usuario = autenticacionService.registrar(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    /**
     * RF-AUTH-02 — Login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
               description = "Retorna access token (15 min) + refresh token (7 días).")
    public ResponseEntity<TokenResponseDTO> login(
            @Valid @RequestBody LoginDTO loginDTO) {
        TokenResponseDTO respuesta = autenticacionService.login(loginDTO);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * RF-AUTH-04 — Refresh token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token",
               description = "Envía el refresh token en Authorization: Bearer <token>")
    public ResponseEntity<TokenResponseDTO> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7) : authHeader;
        return ResponseEntity.ok(autenticacionService.refreshToken(token));
    }

    /**
     * RF-AUTH-05 — Logout
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión",
               description = "Invalida el refresh token en MongoDB.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("X-User-Id") String usuarioId) {
        autenticacionService.logout(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

    /**
     * RF-AUTH-06 — Recuperar contraseña (paso 1)
     * POST /api/auth/recuperar
     */
    @PostMapping("/recuperar")
    @Operation(summary = "Solicitar recuperación de contraseña",
               description = "Envía enlace temporal de 15 min al correo registrado.")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(
            @Valid @RequestBody RecuperarContrasenaDTO dto) {
        autenticacionService.solicitarRecuperacion(dto);
        return ResponseEntity.ok(Map.of("mensaje",
                "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña."));
    }

    /**
     * RF-AUTH-06 — Recuperar contraseña (paso 2)
     * POST /api/auth/restablecer
     */
    @PostMapping("/restablecer")
    @Operation(summary = "Restablecer contraseña",
               description = "Usa el token recibido por correo para cambiar la contraseña.")
    public ResponseEntity<Map<String, String>> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaDTO dto) {
        autenticacionService.restablecerContrasena(dto);
        return ResponseEntity.ok(Map.of("mensaje",
                "Contraseña restablecida exitosamente. Ya puedes iniciar sesión."));
    }

    /**
     * GET /api/auth/me — Perfil del usuario autenticado
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil autenticado",
               description = "Retorna los datos del usuario con sesión activa.",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(autenticacionService.obtenerPorId(usuario.getId()));
    }

    /**
     * GET /api/auth/perfil-completo — Perfil extendido del usuario autenticado
     */
    @GetMapping("/perfil-completo")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener perfil completo",
               description = "Retorna perfil + cantidad de wishlist (0 por ahora) + fechas de registro/último login")
    public ResponseEntity<com.sarichi.crocheting.dto.PerfilCompletoDTO> obtenerPerfilCompleto(
            @AuthenticationPrincipal Usuario usuario) {

        // Usuario autenticado como entidad contiene fechas; la representación pública se obtiene desde el servicio.
        
        com.sarichi.crocheting.dto.UsuarioResponseDTO usuarioDto = autenticacionService.obtenerPorId(usuario.getId());
        com.sarichi.crocheting.dto.PerfilCompletoDTO perfil = com.sarichi.crocheting.dto.PerfilCompletoDTO.builder()
                .usuario(usuarioDto)
                .cantidadWishlist(0L)
                .fechaRegistro(usuario.getFechaRegistro())
                .ultimoLogin(usuario.getUltimoLogin())
                .build();

        return ResponseEntity.ok(perfil);
    }
}
