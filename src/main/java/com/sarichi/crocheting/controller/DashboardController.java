package com.sarichi.crocheting.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.dto.DashboardKpisDTO;
import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.service.DashboardService;
import com.sarichi.crocheting.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dashboard")
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
    public ResponseEntity<List<Map<String, Object>>> ventas() {
        return ResponseEntity.ok(List.of()); // Sprint 3
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMIN','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ventas por categoría")
    public ResponseEntity<List<Map<String, Object>>> categorias() {
        return ResponseEntity.ok(List.of()); // Sprint 3
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
}