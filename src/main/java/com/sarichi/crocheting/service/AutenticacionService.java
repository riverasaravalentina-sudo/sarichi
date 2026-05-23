package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarichi.crocheting.dto.LoginDTO;
import com.sarichi.crocheting.dto.RecuperarContrasenaDTO;
import com.sarichi.crocheting.dto.RegistroDTO;
import com.sarichi.crocheting.dto.RestablecerContrasenaDTO;
import com.sarichi.crocheting.dto.TokenResponseDTO;
import com.sarichi.crocheting.dto.UsuarioResponseDTO;
import com.sarichi.crocheting.entity.UserRole;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.CredencialesInvalidasException;
import com.sarichi.crocheting.exception.TokenInvalidoException;
import com.sarichi.crocheting.exception.UsuarioYaExisteException;
import com.sarichi.crocheting.repository.UsuarioRepository;
import com.sarichi.crocheting.security.JwtTokenProvider;

@Service
public class AutenticacionService {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    // ── REGISTRO ─────────────────────────────────────────────────────────

    public UsuarioResponseDTO registrar(RegistroDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getCorreo());

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new UsuarioYaExisteException(
                    "El correo ya está registrado en el sistema");
        }

        if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Usuario nuevo = Usuario.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .passwordHash(passwordEncoder.encode(dto.getContrasena()))
                .telefono(dto.getTelefono())
                .rol(dto.getRol() != null ? dto.getRol() : UserRole.CLIENTE)
                .estado("ACTIVO")
                .build();

        Usuario guardado = usuarioRepository.save(nuevo);
        log.info("Usuario registrado con ID: {}", guardado.getId());

        return mapearADTO(guardado);
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────

    public TokenResponseDTO login(LoginDTO dto) {
        log.info("Login para: {}", dto.getCorreo());

        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Correo o contraseña inválidos"));

        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña inválidos");
        }

        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new CredencialesInvalidasException(
                    "La cuenta está " + usuario.getEstado().toLowerCase());
        }

        String accessToken  = jwtTokenProvider.generarAccessToken(usuario);
        String refreshToken = jwtTokenProvider.generarRefreshToken(usuario);

        usuario.setRefreshToken(refreshToken);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .usuario(mapearADTO(usuario))
                .build();
    }

    // ── REFRESH TOKEN ─────────────────────────────────────────────────────

    public TokenResponseDTO refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validarToken(refreshToken)) {
            throw new TokenInvalidoException("Refresh token inválido o expirado");
        }

        String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(refreshToken);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Usuario no encontrado"));

        if (!refreshToken.equals(usuario.getRefreshToken())) {
            throw new TokenInvalidoException("Refresh token no reconocido");
        }

        String nuevoAccessToken = jwtTokenProvider.generarAccessToken(usuario);

        return TokenResponseDTO.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .usuario(mapearADTO(usuario))
                .build();
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────

    public void logout(String usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Usuario no encontrado"));
        usuario.setRefreshToken(null);
        usuarioRepository.save(usuario);
        log.info("Logout exitoso para usuario: {}", usuarioId);
    }

    // ── RECUPERAR CONTRASEÑA ──────────────────────────────────────────────

    public void solicitarRecuperacion(RecuperarContrasenaDTO dto) {
        usuarioRepository.findByCorreo(dto.getCorreo()).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            usuario.setTokenRecuperacion(token);
            usuario.setTokenRecuperacionExpira(LocalDateTime.now().plusMinutes(15));
            usuarioRepository.save(usuario);
            emailService.enviarCorreoRecuperacion(
                    usuario.getCorreo(), usuario.getNombre(), token);
        });
    }

    public void restablecerContrasena(RestablecerContrasenaDTO dto) {
        if (!dto.getNuevaContrasena().equals(dto.getConfirmarContrasena())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Usuario usuario = usuarioRepository
                .findByTokenRecuperacion(dto.getToken())
                .orElseThrow(() -> new TokenInvalidoException(
                        "Token de recuperación inválido o expirado"));

        if (LocalDateTime.now().isAfter(usuario.getTokenRecuperacionExpira())) {
            throw new TokenInvalidoException("El token de recuperación ha expirado");
        }

        usuario.setPasswordHash(passwordEncoder.encode(dto.getNuevaContrasena()));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenRecuperacionExpira(null);
        usuarioRepository.save(usuario);
        log.info("Contraseña restablecida para: {}", usuario.getCorreo());
    }

    // ── OBTENER USUARIO ───────────────────────────────────────────────────

    public UsuarioResponseDTO obtenerPorId(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Usuario no encontrado"));
        return mapearADTO(usuario);
    }

    // ── MAPPER ────────────────────────────────────────────────────────────

    private UsuarioResponseDTO mapearADTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .fotoUrl(usuario.getFotoUrl())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .modoOscuro(usuario.isModoOscuro())
                .build();
    }
}