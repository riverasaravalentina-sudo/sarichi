package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.DevolucionDTO;
import com.sarichi.crocheting.dto.SolicitarDevolucionDTO;
import com.sarichi.crocheting.exception.DevolucionException;
import com.sarichi.crocheting.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class DevolucionController {
    private final DevolucionService devolucionService;
    
    @PostMapping("/solicitar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DevolucionDTO> solicitarDevolucion(
            Authentication authentication,
            @RequestBody SolicitarDevolucionDTO dto) {
        try {
            String usuarioId = authentication.getName();
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(devolucionService.solicitarDevolucion(usuarioId, dto));
        } catch (DevolucionException e) {
            throw e;
        }
    }
    
    @GetMapping("/mis-devoluciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DevolucionDTO>> listarMisDevoluciones(Authentication authentication) {
        String usuarioId = authentication.getName();
        return ResponseEntity.ok(devolucionService.listarMisDevoluciones(usuarioId));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICA')")
    public ResponseEntity<List<DevolucionDTO>> listarTodas() {
        return ResponseEntity.ok(devolucionService.listarTodasDevoluciones());
    }
    
    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICA')")
    public ResponseEntity<DevolucionDTO> aprobarDevolucion(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            String observaciones = body.getOrDefault("observaciones", "");
            return ResponseEntity.ok(devolucionService.aprobarDevolucion(id, observaciones));
        } catch (DevolucionException e) {
            throw e;
        }
    }
    
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICA')")
    public ResponseEntity<DevolucionDTO> rechazarDevolucion(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            String observaciones = body.getOrDefault("observaciones", "");
            return ResponseEntity.ok(devolucionService.rechazarDevolucion(id, observaciones));
        } catch (DevolucionException e) {
            throw e;
        }
    }
    
    @PutMapping("/{id}/recibir")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICA')")
    public ResponseEntity<DevolucionDTO> registrarRecepcion(@PathVariable String id) {
        try {
            return ResponseEntity.ok(devolucionService.registrarRecepcion(id));
        } catch (DevolucionException e) {
            throw e;
        }
    }
}
