package com.sarichi.crocheting.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sarichi.crocheting.exception.CredencialesInvalidasException;
import com.sarichi.crocheting.exception.TokenInvalidoException;
import com.sarichi.crocheting.exception.UsuarioYaExisteException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioYaExiste(
            UsuarioYaExisteException ex) {
        return error(HttpStatus.CONFLICT, "Usuario ya existe", ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredenciales(
            CredencialesInvalidasException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", ex.getMessage());
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarToken(
            TokenInvalidoException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Token inválido o expirado", ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioNoEncontrado(
            UsernameNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("estado", 400);
        respuesta.put("error", "Errores de validación");

        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        respuesta.put("campos", campos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoIlegal(
            IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, "Argumento inválido", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", "Por favor intente más tarde");
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("estado", status.value());
        body.put("error", error);
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}