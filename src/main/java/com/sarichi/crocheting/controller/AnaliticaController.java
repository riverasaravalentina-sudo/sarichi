package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.MetricasTraficoDTO;
import com.sarichi.crocheting.service.AnaliticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analitica")
@RequiredArgsConstructor
public class AnaliticaController {
    
    private final AnaliticaService analiticaService;
    
    @PostMapping("/visita")
    public ResponseEntity<Void> registrarVisita(@RequestBody Map<String, String> body) {
        analiticaService.registrarVisita(
                body.get("sessionId"),
                body.get("fuente"),
                body.get("url"),
                body.get("usuarioId"),
                body.get("userAgent"),
                body.get("ipAddress")
        );
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/click")
    public ResponseEntity<Void> registrarClick(@RequestBody Map<String, String> body) {
        analiticaService.registrarClick(
                body.get("sessionId"),
                body.get("url"),
                body.get("productoId")
        );
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/conversion")
    public ResponseEntity<Void> registrarConversion(@RequestBody Map<String, Object> body) {
        analiticaService.registrarConversion(
                (String) body.get("sessionId"),
                (String) body.get("pedidoId"),
                ((Number) body.get("valor")).doubleValue()
        );
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/metricas")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<MetricasTraficoDTO> obtenerMetricasTrafico(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(analiticaService.obtenerMetricasTrafico(desde, hasta));
    }
    
    @GetMapping("/paginas-populares")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<List<String>> obtenerPaginasMasVisitadas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analiticaService.obtenerPaginasMasVisitadas(desde, hasta, limit));
    }
}
