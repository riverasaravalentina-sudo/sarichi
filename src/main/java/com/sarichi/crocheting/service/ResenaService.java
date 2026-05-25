package com.sarichi.crocheting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.ResenaDTO;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.entity.Resena;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.UnauthorizedException;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.ResenaRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ResenaDTO crearResena(String pedidoId, String productoId, String usuarioId, Integer calificacion,
            String comentario) {
        // Validar que el pedido sea ENTREGADO
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (!"ENTREGADO".equals(pedido.getEstado())) {
            throw new UnauthorizedException("Solo se pueden reseñar productos de pedidos entregados");
        }

        // Validar que no exista reseña previa
        if (resenaRepository.existsByPedidoIdAndUsuarioId(pedidoId, usuarioId)) {
            throw new IllegalArgumentException("Ya has reseñado este pedido");
        }

        // Crear reseña
        Resena resena = Resena.builder()
                .pedidoId(pedidoId)
                .productoId(productoId)
                .usuarioId(usuarioId)
                .calificacion(calificacion)
                .comentario(comentario)
                .build();

        Resena resenaGuardada = resenaRepository.save(resena);
        return entityToDto(resenaGuardada);
    }

    public List<ResenaDTO> listarPorProducto(String productoId) {
        return resenaRepository.findByProductoIdOrderByFechaCreacionDesc(productoId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public Double obtenerPromedioCalificacion(String productoId) {
        List<Resena> resenas = resenaRepository.findByProductoId(productoId);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        return resenas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);
    }

    public Long obtenerTotalResenas(String productoId) {
        return (long) resenaRepository.findByProductoId(productoId).size();
    }

    private ResenaDTO entityToDto(Resena resena) {
        Usuario usuario = usuarioRepository.findById(resena.getUsuarioId()).orElse(null);
        String usuarioNombre = usuario != null ? usuario.getNombre() : "Desconocido";

        Producto producto = productoRepository.findById(resena.getProductoId()).orElse(null);
        String productoNombre = producto != null ? producto.getNombre() : "Desconocido";

        return ResenaDTO.builder()
                .id(resena.getId())
                .pedidoId(resena.getPedidoId())
                .productoId(resena.getProductoId())
                .productoNombre(productoNombre)
                .usuarioId(resena.getUsuarioId())
                .usuarioNombre(usuarioNombre)
                .calificacion(resena.getCalificacion())
                .comentario(resena.getComentario())
                .fechaCreacion(resena.getFechaCreacion())
                .build();
    }
}
