package com.sarichi.crocheting.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Pedido;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    List<Pedido> findByUsuarioId(String usuarioId);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByEstadoAndFechaPedidoBetween(String estado, LocalDateTime desde, LocalDateTime hasta);

    Long countByEstado(String estado);

    List<Pedido> findByFechaPedidoBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query(value = "{ 'usuarioId': ?0, 'estado': 'ENTREGADO' }", count = true)
    Long countPedidosEntregadosPorUsuario(String usuarioId);
}
