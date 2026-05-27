package com.sarichi.crocheting.controller;

import com.sarichi.crocheting.dto.ArticuloBlogDTO;
import com.sarichi.crocheting.dto.CrearArticuloDTO;
import com.sarichi.crocheting.service.BlogService;
import com.sarichi.crocheting.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {
    
    private final BlogService blogService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/publicados")
    public ResponseEntity<List<ArticuloBlogDTO>> listarPublicados() {
        return ResponseEntity.ok(blogService.listarPublicados());
    }
    
    @GetMapping("/{slug}")
    public ResponseEntity<ArticuloBlogDTO> obtenerPorSlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.obtenerPorSlug(slug));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ArticuloBlogDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(blogService.buscarPorCategoria(categoria));
    }
    
    @GetMapping("/recientes")
    public ResponseEntity<List<ArticuloBlogDTO>> obtenerRecientes(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(blogService.obtenerArticulosRecientes(limit));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<ArticuloBlogDTO> crearArticulo(
            @RequestBody CrearArticuloDTO dto,
            @RequestHeader("Authorization") String token) {
        String usuarioId = jwtTokenProvider.getUsuarioIdFromToken(token.replace("Bearer ", ""));
        
        ArticuloBlogDTO resultado = blogService.crearArticulo(dto, usuarioId, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<ArticuloBlogDTO> actualizarArticulo(
            @PathVariable String id,
            @RequestBody CrearArticuloDTO dto) {
        return ResponseEntity.ok(blogService.actualizarArticulo(id, dto));
    }
    
    @PutMapping("/{id}/publicar")
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<ArticuloBlogDTO> publicarArticulo(@PathVariable String id) {
        return ResponseEntity.ok(blogService.publicarArticulo(id));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarArticulo(@PathVariable String id) {
        blogService.eliminarArticulo(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MERCADEO', 'ADMIN')")
    public ResponseEntity<List<ArticuloBlogDTO>> listarTodos() {
        return ResponseEntity.ok(blogService.listarTodos());
    }
}
