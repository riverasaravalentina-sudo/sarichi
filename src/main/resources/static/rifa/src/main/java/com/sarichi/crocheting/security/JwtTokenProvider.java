package com.sarichi.crocheting.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.TokenInvalidoException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component

public class JwtTokenProvider {

	private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarAccessToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().toString());
        claims.put("nombre", usuario.getNombre());
        claims.put("correo", usuario.getCorreo());
        return crearToken(claims, usuario.getId(), jwtExpirationMs);
    }

    public String generarRefreshToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tipo", "REFRESH");
        return crearToken(claims, usuario.getId(), jwtRefreshExpirationMs);
    }

    private String crearToken(Map<String, Object> claims, String usuarioId, int expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(usuarioId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsuarioIdFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getRolFromToken(String token) {
        return (String) getAllClaimsFromToken(token).get("rol");
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Firma JWT inválida: {}", ex.getMessage());
            throw new TokenInvalidoException("Firma JWT inválida");
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido: {}", ex.getMessage());
            throw new TokenInvalidoException("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
            throw new TokenInvalidoException("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado: {}", ex.getMessage());
            throw new TokenInvalidoException("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            log.error("Claims JWT vacías: {}", ex.getMessage());
            throw new TokenInvalidoException("Claims JWT vacías");
        }
    }

    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}