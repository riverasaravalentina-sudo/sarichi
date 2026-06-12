package com.sarichi.crocheting.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarichi.crocheting.dto.PagoDTO;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Inicio de pagos Wompi y webhook")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/iniciar")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Iniciar pago con Wompi",
            description = "Devuelve redirectUrl. Si WOMPI_PUBLIC_KEY no existe, funciona en modo mock.")
    public ResponseEntity<PagoDTO> iniciarPago(@RequestBody PagoDTO request,
                                               @AuthenticationPrincipal Usuario usuario) {
        String usuarioId = usuario == null ? null : usuario.getId();
        return ResponseEntity.ok(pagoService.iniciarPago(request, usuarioId));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Webhook de Wompi",
            description = "Recibe eventos de Wompi y actualiza el estado del pago por referencia.")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody Map<String, Object> payload) {
        pagoService.procesarWebhook(payload);
        return ResponseEntity.ok(Map.of("mensaje", "Webhook recibido"));
    }
}
