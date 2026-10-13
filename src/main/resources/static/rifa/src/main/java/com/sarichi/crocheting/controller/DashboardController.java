package com.sarichi.crocheting.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.dto.DashboardKpisDTO;
import com.sarichi.crocheting.dto.EstadisticasLogisticaDTO;
import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.dto.VentasPorCategoriaDTO;
import com.sarichi.crocheting.dto.VentasPorPeriodoDTO;
import com.sarichi.crocheting.service.DashboardService;
import com.sarichi.crocheting.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "KPIs y métricas por rol")

public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('ADMIN','ARTESANA','BODEGA','LOGISTICA','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "KPIs generales del dashboard")
    public ResponseEntity<DashboardKpisDTO> obtenerKpis() {
        return ResponseEntity.ok(dashboardService.obtenerKpis());
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ventas por período")
    public ResponseEntity<List<VentasPorPeriodoDTO>> ventas(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        
        LocalDateTime desdeDate = desde != null ? 
            LocalDateTime.parse(desde + "T00:00:00") : 
            LocalDateTime.now().minusDays(30);
        
        LocalDateTime hastaDate = hasta != null ? 
            LocalDateTime.parse(hasta + "T23:59:59") : 
            LocalDateTime.now();

        return ResponseEntity.ok(dashboardService.obtenerVentasPorPeriodo(desdeDate, hastaDate));
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMIN','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ventas por categoría")
    public ResponseEntity<List<VentasPorCategoriaDTO>> categorias() {
        return ResponseEntity.ok(dashboardService.obtenerVentasPorCategoria());
    }

    @GetMapping("/top-productos")
    @PreAuthorize("hasAnyRole('ADMIN','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Top 5 productos más vendidos")
    public ResponseEntity<List<Map<String, Object>>> topProductos() {
        return ResponseEntity.ok(dashboardService.obtenerTopProductos());
    }

    @GetMapping("/pedidos-recientes")
    @PreAuthorize("hasAnyRole('ADMIN','ARTESANA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Últimos 10 pedidos")
    public ResponseEntity<List<Map<String, Object>>> pedidosRecientes() {
        return ResponseEntity.ok(List.of()); // Sprint 4
    }

    @GetMapping("/despachos-hoy")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Despachos del día")
    public ResponseEntity<List<Map<String, Object>>> despachosHoy() {
        return ResponseEntity.ok(List.of()); // Sprint 4
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Productos con stock crítico")
    public ResponseEntity<List<ProductoDTO>> stockCritico() {
        return ResponseEntity.ok(productoService.obtenerStockCritico(2));
    }

    @GetMapping("/logistica")
    @PreAuthorize("hasAnyRole('LOGISTICA','ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Métricas de logística")
    public ResponseEntity<EstadisticasLogisticaDTO> metricasLogistica() {
        return ResponseEntity.ok(dashboardService.obtenerMetricasLogistica());
    }

    @GetMapping("/produccion")
    @PreAuthorize("hasAnyRole('ARTESANA','ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Métricas de producción")
    public ResponseEntity<Map<String, Object>> metricasProduccion() {
        return ResponseEntity.ok(dashboardService.obtenerMetricasProduccion());
    }
}