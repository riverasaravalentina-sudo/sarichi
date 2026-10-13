package com.sarichi.crocheting.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarichi.crocheting.dto.WishlistDTO;
import com.sarichi.crocheting.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<WishlistDTO>> listar(@PathVariable String usuarioId) {
        return ResponseEntity.ok(wishlistService.listarPorUsuario(usuarioId));
    }

    @PostMapping("/{usuarioId}/{productoId}")
    public ResponseEntity<WishlistDTO> agregar(@PathVariable String usuarioId,
                                                @PathVariable String productoId) {
        try {
            return ResponseEntity.ok(wishlistService.agregar(usuarioId, productoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{usuarioId}/{productoId}")
    public ResponseEntity<Void> eliminar(@PathVariable String usuarioId,
                                          @PathVariable String productoId) {
        wishlistService.eliminar(usuarioId, productoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{usuarioId}/{productoId}/notificar")
    public ResponseEntity<WishlistDTO> toggleNotificar(@PathVariable String usuarioId,
                                                        @PathVariable String productoId) {
        try {
            return ResponseEntity.ok(wishlistService.toggleNotificar(usuarioId, productoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificar(@RequestBody Map<String, String> body) {
        boolean existe = wishlistService.existe(body.get("usuarioId"), body.get("productoId"));
        return ResponseEntity.ok(Map.of("existe", existe));
    }
}
