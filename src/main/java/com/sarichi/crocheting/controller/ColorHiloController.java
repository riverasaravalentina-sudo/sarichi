package com.sarichi.crocheting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.dto.ColorHiloDTO;
import com.sarichi.crocheting.service.ColorHiloService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller para CRUD de colores de hilo.
 */
@RestController
@RequestMapping("/colores-hilo")
@Tag(name = "Inventario Hilos", description = "Gestión de colores de hilo")
public class ColorHiloController {

    @Autowired
    private ColorHiloService colorHiloService;

    // GET /colores-hilo — público
    @GetMapping
    @Operation(summary = "Listar colores de hilo")
    public ResponseEntity<List<ColorHiloDTO>> listar() {
        return ResponseEntity.ok(colorHiloService.listarTodos());
    }

    // GET /colores-hilo/{id} — público
    @GetMapping("/{id}")
    @Operation(summary = "Obtener color por ID")
    public ResponseEntity<ColorHiloDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(colorHiloService.obtenerPorId(id));
    }

    // GET /colores-hilo/criticos — ADMIN o BODEGA
    @GetMapping("/criticos")
    @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Colores críticos", description = "Colores cuyo stock está por debajo del mínimo")
    public ResponseEntity<List<ColorHiloDTO>> criticos() {
        return ResponseEntity.ok(colorHiloService.obtenerCriticos());
    }

    // POST /colores-hilo — ADMIN o BODEGA
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear color de hilo")
    public ResponseEntity<ColorHiloDTO> crear(@Valid @RequestBody ColorHiloDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorHiloService.crear(dto));
    }

    // PUT /colores-hilo/{id} — ADMIN o BODEGA
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','BODEGA')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar color de hilo")
    public ResponseEntity<ColorHiloDTO> actualizar(@PathVariable String id, @Valid @RequestBody ColorHiloDTO dto) {
        return ResponseEntity.ok(colorHiloService.actualizar(id, dto));
    }

    // DELETE /colores-hilo/{id} — solo ADMIN (eliminación lógica)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar color de hilo (lógico)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        colorHiloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
