package com.sarichi.crocheting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarichi.crocheting.dto.ResenaDTO;
import com.sarichi.crocheting.service.ResenaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/resenas")
@Tag(name = "Reseñas", description = "Gestión de reseñas de productos")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResenaDTO crearResena(@RequestBody Map<String, Object> body, Authentication auth) {
        String usuarioId = auth.getName();
        String pedidoId = (String) body.get("pedidoId");
        String productoId = (String) body.get("productoId");
        Integer calificacion = ((Number) body.get("calificacion")).intValue();
        String comentario = (String) body.get("comentario");

        return resenaService.crearResena(pedidoId, productoId, usuarioId, calificacion, comentario);
    }

    @GetMapping("/producto/{productoId}")
    public List<ResenaDTO> listarPorProducto(@PathVariable String productoId) {
        return resenaService.listarPorProducto(productoId);
    }

    @GetMapping("/producto/{productoId}/promedio")
    public Map<String, Object> obtenerPromedioCalificacion(@PathVariable String productoId) {
        Double promedio = resenaService.obtenerPromedioCalificacion(productoId);
        Long totalResenas = resenaService.obtenerTotalResenas(productoId);

        Map<String, Object> response = new HashMap<>();
        response.put("productoId", productoId);
        response.put("promedio", promedio);
        response.put("totalResenas", totalResenas);

        return response;
    }
}
