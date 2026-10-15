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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.sarichi.crocheting.exception.ClaveInvalidaException;
import com.sarichi.crocheting.exception.ConflictoReservaException;
import com.sarichi.crocheting.exception.CredencialesInvalidasException;
import com.sarichi.crocheting.exception.DespachoException;
import com.sarichi.crocheting.exception.DevolucionException;
import com.sarichi.crocheting.exception.PanelNoConfiguradoException;
import com.sarichi.crocheting.exception.ProduccionException;
import com.sarichi.crocheting.exception.StockInsuficienteException;
import com.sarichi.crocheting.exception.TokenInvalidoException;
import com.sarichi.crocheting.exception.UnauthorizedException;
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

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(
            StockInsuficienteException ex) {
        return error(HttpStatus.CONFLICT, "Stock insuficiente", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> manejarUnauthorized(
            UnauthorizedException ex) {
        return error(HttpStatus.FORBIDDEN, "No autorizado", ex.getMessage());
    }

    @ExceptionHandler(DespachoException.class)
    public ResponseEntity<Map<String, Object>> manejarDespacho(
            DespachoException ex) {
        return error(HttpStatus.CONFLICT, "Error en despacho", ex.getMessage());
    }

    @ExceptionHandler(DevolucionException.class)
    public ResponseEntity<Map<String, Object>> manejarDevolucion(
            DevolucionException ex) {
        return error(HttpStatus.CONFLICT, "Error en devolución", ex.getMessage());
    }

    @ExceptionHandler(ProduccionException.class)
    public ResponseEntity<Map<String, Object>> manejarProduccion(
            ProduccionException ex) {
        return error(HttpStatus.CONFLICT, "Error en producción", ex.getMessage());
    }

    @ExceptionHandler(ConflictoReservaException.class)
    public ResponseEntity<Map<String, Object>> manejarConflictoReserva(
            ConflictoReservaException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("estado", HttpStatus.CONFLICT.value());
        body.put("error", "Números no disponibles");
        body.put("mensaje", ex.getMessage());
        body.put("numerosConflicto", ex.getNumerosConflicto());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ClaveInvalidaException.class)
    public ResponseEntity<Map<String, Object>> manejarClaveInvalida(
            ClaveInvalidaException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Clave inválida", ex.getMessage());
    }

    @ExceptionHandler(PanelNoConfiguradoException.class)
    public ResponseEntity<Map<String, Object>> manejarPanelNoConfigurado(
            PanelNoConfiguradoException ex) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Panel no configurado",
                ex.getMessage());
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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(
            NoResourceFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "Recurso no encontrado",
                "La ruta solicitada no existe");
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