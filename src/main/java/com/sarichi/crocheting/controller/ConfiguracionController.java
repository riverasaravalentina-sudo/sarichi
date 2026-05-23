package com.sarichi.crocheting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sarichi.crocheting.entity.Configuracion;
import com.sarichi.crocheting.service.ConfiguracionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para obtener y actualizar la configuración global.
 */
@RestController
@RequestMapping("/configuracion")
@Tag(name = "Configuración", description = "Parámetros globales de la aplicación")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping
    @Operation(summary = "Obtener configuración actual")
    public ResponseEntity<Configuracion> obtener() {
        return ResponseEntity.ok(configuracionService.obtenerConfiguracion());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar configuración (Admin)")
    public ResponseEntity<Configuracion> actualizar(@RequestBody Configuracion nueva) {
        return ResponseEntity.ok(configuracionService.actualizarConfiguracion(nueva));
    }
}
