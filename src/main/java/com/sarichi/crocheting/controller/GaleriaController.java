package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.ColeccionGaleriaDTO;
import com.sarichi.crocheting.dto.FotoGaleriaDTO;
import com.sarichi.crocheting.service.GaleriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/galeria")
@RequiredArgsConstructor
public class GaleriaController {
    
    private final GaleriaService galeriaService;
    
    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionGaleriaDTO>> listarColecciones() {
        return ResponseEntity.ok(galeriaService.listarColeccionesActivas());
    }
    
    @GetMapping("/coleccion/{slug}")
    public ResponseEntity<ColeccionGaleriaDTO> obtenerColeccionPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(galeriaService.obtenerColeccionPorSlug(slug));
    }
    
    @GetMapping("/recientes")
    public ResponseEntity<List<FotoGaleriaDTO>> listarFotosRecientes(
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(galeriaService.listarFotosRecientes(limit));
    }
    
    @PostMapping("/colecciones")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<ColeccionGaleriaDTO> crearColeccion(@RequestBody Map<String, String> body) {
        ColeccionGaleriaDTO resultado = galeriaService.crearColeccion(
                body.get("nombre"),
                body.get("descripcion"),
                body.get("portadaUrl")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @PostMapping("/colecciones/{id}/fotos")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<FotoGaleriaDTO> agregarFoto(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        FotoGaleriaDTO resultado = galeriaService.agregarFotoAColeccion(
                id,
                (String) body.get("urlFoto"),
                (String) body.get("titulo"),
                (String) body.get("descripcion"),
                (String) body.get("historia"),
                ((Number) body.get("tiempoElaboracionHoras")).intValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/colecciones/{id}/fotos")
    public ResponseEntity<List<FotoGaleriaDTO>> listarFotosPorColeccion(@PathVariable String id) {
        return ResponseEntity.ok(galeriaService.listarFotosPorColeccion(id));
    }
    
    @PutMapping("/fotos/{id}/vincular-producto")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<FotoGaleriaDTO> vincularProducto(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(galeriaService.vincularFotoAProducto(id, body.get("productoId")));
    }
    
    @DeleteMapping("/fotos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarFoto(@PathVariable String id) {
        galeriaService.eliminarFoto(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/fotos/{id}/like")
    public ResponseEntity<FotoGaleriaDTO> darLike(@PathVariable String id) {
        return ResponseEntity.ok(galeriaService.darLikeAFoto(id));
    }
}
