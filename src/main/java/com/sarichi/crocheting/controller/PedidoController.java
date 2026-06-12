package com.sarichi.crocheting.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarichi.crocheting.dto.CrearPedidoDTO;
import com.sarichi.crocheting.dto.PedidoDTO;
import com.sarichi.crocheting.security.JwtTokenProvider;
import com.sarichi.crocheting.service.PedidoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PedidoDTO crearPedido(@RequestBody CrearPedidoDTO dto, Authentication auth) {
        String usuarioId = auth.getName();
        return pedidoService.crearPedido(usuarioId, dto);
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("isAuthenticated()")
    public List<PedidoDTO> listarMisPedidos(Authentication auth) {
        String usuarioId = auth.getName();
        return pedidoService.listarMisPedidos(usuarioId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTESANA')")
    public List<PedidoDTO> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTESANA')")
    public PedidoDTO obtenerPorId(@PathVariable String id) {
        return pedidoService.obtenerPorId(id);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTESANA')")
    public PedidoDTO actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        return pedidoService.actualizarEstado(id, nuevoEstado);
    }
}
