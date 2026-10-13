package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Resena;

@Repository
public interface ResenaRepository extends MongoRepository<Resena, String> {

    List<Resena> findByProductoId(String productoId);

    List<Resena> findByUsuarioId(String usuarioId);

    boolean existsByPedidoIdAndUsuarioId(String pedidoId, String usuarioId);

    List<Resena> findByProductoIdOrderByFechaCreacionDesc(String productoId);
}
