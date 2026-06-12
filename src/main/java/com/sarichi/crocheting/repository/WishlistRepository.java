package com.sarichi.crocheting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Wishlist;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    List<Wishlist> findByUsuarioIdOrderByFechaAgregadoDesc(String usuarioId);

    Optional<Wishlist> findByUsuarioIdAndProductoId(String usuarioId, String productoId);

    boolean existsByUsuarioIdAndProductoId(String usuarioId, String productoId);

    void deleteByUsuarioIdAndProductoId(String usuarioId, String productoId);
}
