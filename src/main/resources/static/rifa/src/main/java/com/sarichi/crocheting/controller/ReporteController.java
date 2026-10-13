package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.ReporteVentasDTO;
import com.sarichi.crocheting.dto.ReporteInventarioDTO;
import com.sarichi.crocheting.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    
    private final ReporteService reporteService;
    
    @GetMapping("/ventas/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarVentasPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        
        byte[] pdfBytes = reporteService.generarReporteVentasPDF(desde, hasta);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-ventas.pdf");
        
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
    
    @GetMapping("/inventario/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'BODEGA')")
    public ResponseEntity<byte[]> descargarInventarioExcel() {
        
        byte[] excelBytes = reporteService.generarReporteInventarioExcel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte-inventario.xlsx");
        
        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
    
    @GetMapping("/ventas/datos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteVentasDTO> obtenerDatosVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        
        return ResponseEntity.ok(reporteService.obtenerDatosVentas(desde, hasta));
    }
    
    @GetMapping("/inventario/datos")
    @PreAuthorize("hasAnyRole('ADMIN', 'BODEGA')")
    public ResponseEntity<ReporteInventarioDTO> obtenerDatosInventario() {
        return ResponseEntity.ok(reporteService.obtenerDatosInventario());
    }
}
