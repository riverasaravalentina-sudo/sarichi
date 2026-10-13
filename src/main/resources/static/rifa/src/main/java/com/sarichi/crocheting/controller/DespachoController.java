package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.ActualizarDespachoDTO;
import com.sarichi.crocheting.dto.CrearDespachoDTO;
import com.sarichi.crocheting.dto.DespachoDTO;
import com.sarichi.crocheting.exception.DespachoException;
import com.sarichi.crocheting.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {
    private final DespachoService despachoService;
    
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<List<DespachoDTO>> listarPedidosPendientes() {
        return ResponseEntity.ok(despachoService.listarDespachosPendientes());
    }
    
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<List<DespachoDTO>> listarDespachosHoy() {
        return ResponseEntity.ok(despachoService.listarDespachosHoy());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<DespachoDTO> crearDespacho(@RequestBody CrearDespachoDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crearDespacho(dto));
        } catch (DespachoException e) {
            throw e;
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<List<DespachoDTO>> listarTodos() {
        // Retorna los despachos del día
        return ResponseEntity.ok(despachoService.listarDespachosHoy());
    }
    
    @GetMapping("/seguimiento/{numeroGuia}")
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN', 'CLIENTE')")
    public ResponseEntity<Map<String, Object>> consultarSeguimiento(@PathVariable String numeroGuia) {
        try {
            return ResponseEntity.ok(despachoService.consultarSeguimiento(numeroGuia));
        } catch (DespachoException e) {
            throw e;
        }
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<DespachoDTO> actualizarEstado(
            @PathVariable String id,
            @RequestBody ActualizarDespachoDTO dto) {
        try {
            return ResponseEntity.ok(despachoService.actualizarEstadoDespacho(id, dto));
        } catch (DespachoException e) {
            throw e;
        }
    }
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyRole('LOGISTICA', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(despachoService.obtenerEstadisticas());
    }
}
