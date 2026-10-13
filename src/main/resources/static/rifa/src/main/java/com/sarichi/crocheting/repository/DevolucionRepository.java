package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.Devolucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DevolucionRepository extends MongoRepository<Devolucion, String> {
    List<Devolucion> findByUsuarioId(String usuarioId);
    Optional<Devolucion> findByPedidoId(String pedidoId);
    List<Devolucion> findByEstado(String estado);
    List<Devolucion> findByFechaSolicitudBetween(LocalDateTime inicio, LocalDateTime fin);
}
