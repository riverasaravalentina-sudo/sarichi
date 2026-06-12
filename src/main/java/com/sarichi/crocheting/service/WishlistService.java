package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.WishlistDTO;
import com.sarichi.crocheting.entity.Wishlist;
import com.sarichi.crocheting.repository.WishlistRepository;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductoService productoService;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductoService productoService) {
        this.wishlistRepository = wishlistRepository;
        this.productoService = productoService;
    }

    public List<WishlistDTO> listarPorUsuario(String usuarioId) {
        return wishlistRepository.findByUsuarioIdOrderByFechaAgregadoDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public WishlistDTO agregar(String usuarioId, String productoId) {
        if (wishlistRepository.existsByUsuarioIdAndProductoId(usuarioId, productoId)) {
            throw new IllegalArgumentException("El producto ya está en tu lista de deseos");
        }
        var producto = productoService.obtenerPorId(productoId);
        Wishlist item = Wishlist.builder()
                .usuarioId(usuarioId)
                .productoId(productoId)
                .productoNombre(producto.getNombre())
                .precioAlAgregar(producto.getPrecioBase())
                .categoria(producto.getCategoria())
                .fechaAgregado(LocalDateTime.now())
                .notificarSiBajaPrecio(false)
                .build();
        return toDTO(wishlistRepository.save(item));
    }

    public void eliminar(String usuarioId, String productoId) {
        wishlistRepository.deleteByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    public boolean existe(String usuarioId, String productoId) {
        return wishlistRepository.existsByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    public WishlistDTO toggleNotificar(String usuarioId, String productoId) {
        var item = wishlistRepository.findByUsuarioIdAndProductoId(usuarioId, productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en tu wishlist"));
        item.setNotificarSiBajaPrecio(!item.isNotificarSiBajaPrecio());
        return toDTO(wishlistRepository.save(item));
    }

    private WishlistDTO toDTO(Wishlist w) {
        return WishlistDTO.builder()
                .id(w.getId())
                .productoId(w.getProductoId())
                .productoNombre(w.getProductoNombre())
                .precioAlAgregar(w.getPrecioAlAgregar())
                .categoria(w.getCategoria())
                .fechaAgregado(w.getFechaAgregado())
                .notificarSiBajaPrecio(w.isNotificarSiBajaPrecio())
                .build();
    }
}
