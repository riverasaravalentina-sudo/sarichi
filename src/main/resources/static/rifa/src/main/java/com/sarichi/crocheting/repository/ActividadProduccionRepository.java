package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.ActividadProduccion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadProduccionRepository extends MongoRepository<ActividadProduccion, String> {
    List<ActividadProduccion> findByArtesanaId(String artesanaId);
    List<ActividadProduccion> findByEstado(String estado);
    Optional<ActividadProduccion> findByPedidoId(String pedidoId);
    List<ActividadProduccion> findByArtesanaIdAndEstado(String artesanaId, String estado);
    long countByArtesanaIdAndEstado(String artesanaId, String estado);
}
