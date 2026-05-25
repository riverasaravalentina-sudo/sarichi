package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.CrearPedidoDTO;
import com.sarichi.crocheting.dto.ItemPedidoDTO;
import com.sarichi.crocheting.dto.ItemPedidoRequest;
import com.sarichi.crocheting.dto.PedidoDTO;
import com.sarichi.crocheting.entity.ItemPedido;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.StockInsuficienteException;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public PedidoDTO crearPedido(String usuarioId, CrearPedidoDTO dto) {
        // Validar stock disponible
        for (ItemPedidoRequest item : dto.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para " + producto.getNombre() + ". Disponible: " + producto.getStock());
            }
        }

        // Calcular total y crear items
        Double total = 0.0;
        List<ItemPedido> items = new java.util.ArrayList<>();

        for (ItemPedidoRequest itemRequest : dto.getItems()) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId()).get();
            Double subtotal = producto.getPrecioBase() * itemRequest.getCantidad();

            items.add(ItemPedido.builder()
                    .productoId(producto.getId())
                    .nombreProducto(producto.getNombre())
                    .cantidad(itemRequest.getCantidad())
                    .colorSolicitado(itemRequest.getColorSolicitado())
                    .precioUnitario(producto.getPrecioBase())
                    .subtotal(subtotal)
                    .build());

            total += subtotal;

            // Descontar stock
            producto.setStock(producto.getStock() - itemRequest.getCantidad());
            productoRepository.save(producto);
        }

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .usuarioId(usuarioId)
                .items(items)
                .total(total)
                .estado("PENDIENTE")
                .direccionEnvio(dto.getDireccionEnvio())
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return entityToDto(pedidoGuardado);
    }

    public List<PedidoDTO> listarMisPedidos(String usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public PedidoDTO obtenerPorId(String pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .map(this::entityToDto)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    public PedidoDTO actualizarEstado(String pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        return entityToDto(pedidoRepository.save(pedido));
    }

    public Long contarPorEstado(String estado) {
        return pedidoRepository.countByEstado(estado);
    }

    public List<PedidoDTO> obtenerPedidosPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return pedidoRepository.findByFechaPedidoBetween(desde, hasta)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    private PedidoDTO entityToDto(Pedido pedido) {
        Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId()).orElse(null);
        String usuarioNombre = usuario != null ? usuario.getNombre() : "Desconocido";

        List<ItemPedidoDTO> itemsDto = pedido.getItems().stream()
                .map(item -> ItemPedidoDTO.builder()
                        .productoId(item.getProductoId())
                        .nombreProducto(item.getNombreProducto())
                        .cantidad(item.getCantidad())
                        .colorSolicitado(item.getColorSolicitado())
                        .precioUnitario(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoDTO.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuarioId())
                .usuarioNombre(usuarioNombre)
                .fechaPedido(pedido.getFechaPedido())
                .items(itemsDto)
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .direccionEnvio(pedido.getDireccionEnvio())
                .build();
    }
}
