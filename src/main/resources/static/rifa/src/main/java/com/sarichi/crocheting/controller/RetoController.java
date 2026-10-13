package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.RetoMensualDTO;
import com.sarichi.crocheting.dto.ParticipacionRetoDTO;
import com.sarichi.crocheting.service.RetoService;
import com.sarichi.crocheting.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retos")
@RequiredArgsConstructor
public class RetoController {
    
    private final RetoService retoService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/activos")
    public ResponseEntity<List<RetoMensualDTO>> listarRetosActivos() {
        return ResponseEntity.ok(retoService.listarRetosActivos());
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<List<RetoMensualDTO>> listarRetosTodos() {
        return ResponseEntity.ok(retoService.listarRetosTodos());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<RetoMensualDTO> crearReto(@RequestBody RetoMensualDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(retoService.crearReto(dto));
    }
    
    @PostMapping("/{id}/participar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParticipacionRetoDTO> participarEnReto(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String token) {
        
        String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(token.replace("Bearer ", ""));
        
        ParticipacionRetoDTO resultado = retoService.participarEnReto(
                id,
                usuarioId,
                usuarioId,
                body.get("urlFotoParticipacion"),
                body.get("descripcion")
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}/participaciones")
    public ResponseEntity<List<ParticipacionRetoDTO>> listarParticipaciones(@PathVariable String id) {
        return ResponseEntity.ok(retoService.listarParticipaciones(id));
    }
    
    @PostMapping("/{id}/participaciones/{pid}/votar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParticipacionRetoDTO> votarParticipacion(
            @PathVariable String id,
            @PathVariable String pid,
            @RequestHeader("Authorization") String token) {
        
        String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(token.replace("Bearer ", ""));
        
        ParticipacionRetoDTO resultado = retoService.votarParticipacion(pid, usuarioId);
        return ResponseEntity.ok(resultado);
    }
    
    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<RetoMensualDTO> finalizarReto(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        
        return ResponseEntity.ok(retoService.finalizarReto(id, body.get("ganadorId")));
    }
}
