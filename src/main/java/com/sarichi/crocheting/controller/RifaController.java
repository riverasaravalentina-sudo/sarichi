package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.CambioEstadoRifaDTO;
import com.sarichi.crocheting.dto.DatosReservaRifaDTO;
import com.sarichi.crocheting.dto.PanelRifaDTO;
import com.sarichi.crocheting.dto.ReservaRifaRequestDTO;
import com.sarichi.crocheting.dto.ReservaRifaResponseDTO;
import com.sarichi.crocheting.dto.TableroRifaDTO;
import com.sarichi.crocheting.security.RifaKeyValidator;
import com.sarichi.crocheting.service.RifaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API de la rifa.
 *
 * Público:  GET /numeros, POST /reservar, DELETE /reservar/{codigo}
 * Privado:  GET /panel, POST /numeros/estado, POST /liberar
 *           (requieren el header X-Rifa-Key = RIFA_ADMIN_KEY)
 */
@RestController
@RequestMapping("/api/rifa")
@RequiredArgsConstructor
public class RifaController {

    private final RifaService rifaService;
    private final RifaKeyValidator rifaKeyValidator;

    // ─── Público ───────────────────────────────────────────────────────

    @GetMapping("/numeros")
    public ResponseEntity<TableroRifaDTO> tablero() {
        return ResponseEntity.ok(rifaService.obtenerTablero());
    }

    @PostMapping("/reservar")
    public ResponseEntity<ReservaRifaResponseDTO> reservar(
            @Valid @RequestBody ReservaRifaRequestDTO request) {
        return ResponseEntity.ok(rifaService.reservar(
                request.getNumeros(),
                request.getCompradorNombre(),
                request.getCompradorTelefono(),
                request.getNotas()));
    }

    @PostMapping("/reservar/{codigo}/datos")
    public ResponseEntity<ReservaRifaResponseDTO> registrarDatos(
            @PathVariable String codigo,
            @RequestBody DatosReservaRifaDTO request) {
        return ResponseEntity.ok(rifaService.registrarDatosReserva(
                codigo,
                request.getCompradorNombre(),
                request.getCompradorTelefono(),
                request.getNotas()));
    }

    @DeleteMapping("/reservar/{codigo}")
    public ResponseEntity<Map<String, Object>> liberar(@PathVariable String codigo) {
        int liberados = rifaService.liberarPorCodigo(codigo);
        return ResponseEntity.ok(Map.of(
                "codigoReserva", codigo,
                "liberados", liberados));
    }

    // ─── Privado (X-Rifa-Key) ──────────────────────────────────────────

    @GetMapping("/panel")
    public ResponseEntity<PanelRifaDTO> panel(
            @RequestHeader(value = "X-Rifa-Key", required = false) String clave) {
        rifaKeyValidator.validar(clave);
        return ResponseEntity.ok(rifaService.obtenerPanel());
    }

    @PostMapping("/numeros/estado")
    public ResponseEntity<PanelRifaDTO> cambiarEstado(
            @RequestHeader(value = "X-Rifa-Key", required = false) String clave,
            @Valid @RequestBody CambioEstadoRifaDTO request) {
        rifaKeyValidator.validar(clave);
        return ResponseEntity.ok(rifaService.cambiarEstado(request, "panel"));
    }

    @PostMapping("/liberar")
    public ResponseEntity<Map<String, Object>> liberarPanel(
            @RequestHeader(value = "X-Rifa-Key", required = false) String clave,
            @RequestParam(value = "codigo", required = false) String codigo) {
        rifaKeyValidator.validar(clave);
        int liberados = rifaService.liberarPorCodigo(codigo);
        return ResponseEntity.ok(Map.of(
                "codigoReserva", codigo == null ? "" : codigo,
                "liberados", liberados));
    }
}
