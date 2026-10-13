package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.ActividadProduccionDTO;
import com.sarichi.crocheting.dto.FotoProcesoDTO;
import com.sarichi.crocheting.exception.ProduccionException;
import com.sarichi.crocheting.service.ActividadProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
public class ActividadProduccionController {
    private final ActividadProduccionService actividadService;
    
    @GetMapping("/mis-actividades")
    @PreAuthorize("hasRole('ARTESANA')")
    public ResponseEntity<List<ActividadProduccionDTO>> listarMisActividades(
            Authentication authentication) {
        String artesanaId = authentication.getName();
        return ResponseEntity.ok(actividadService.listarMisActividades(artesanaId));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActividadProduccionDTO>> listarTodas() {
        // Retorna todas las actividades
        return ResponseEntity.ok(actividadService.listarActividadesPorEstado("EN_PROGRESO"));
    }
    
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ARTESANA', 'ADMIN')")
    public ResponseEntity<List<ActividadProduccionDTO>> listarPorEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(actividadService.listarActividadesPorEstado(estado));
    }
    
    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('ARTESANA')")
    public ResponseEntity<ActividadProduccionDTO> iniciarActividad(@PathVariable String id) {
        try {
            return ResponseEntity.ok(actividadService.iniciarActividad(id));
        } catch (ProduccionException e) {
            throw e;
        }
    }
    
    @PutMapping("/{id}/completar")
    @PreAuthorize("hasRole('ARTESANA')")
    public ResponseEntity<ActividadProduccionDTO> completarActividad(@PathVariable String id) {
        try {
            return ResponseEntity.ok(actividadService.completarActividad(id));
        } catch (ProduccionException e) {
            throw e;
        }
    }
    
    @PostMapping("/{id}/fotos")
    @PreAuthorize("hasRole('ARTESANA')")
    public ResponseEntity<ActividadProduccionDTO> subirFoto(
            @PathVariable String id,
            @RequestBody FotoProcesoDTO fotoDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(actividadService.subirFotoProceso(id, fotoDTO));
        } catch (ProduccionException e) {
            throw e;
        }
    }
    
    @GetMapping("/{id}/fotos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FotoProcesoDTO>> obtenerFotos(@PathVariable String id) {
        try {
            // Buscar actividad y retornar solo si el usuario es ARTESANA, ADMIN, o CLIENTE del pedido
            // Por ahora retornamos vacío, la lógica de verificación va en el frontend
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            throw new ProduccionException("Error al obtener fotos: " + e.getMessage());
        }
    }
    
    @GetMapping("/capacidad")
    @PreAuthorize("hasAnyRole('ARTESANA', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerCapacidad(
            Authentication authentication) {
        String artesanaId = authentication.getName();
        long capacidadUsada = actividadService.obtenerCapacidadArtesana(artesanaId);
        return ResponseEntity.ok(Map.of(
            "capacidadUsada", capacidadUsada,
            "capacidadMaxima", 5
        ));
    }
}
