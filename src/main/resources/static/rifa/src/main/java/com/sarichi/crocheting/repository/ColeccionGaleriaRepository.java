package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.ColeccionGaleria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColeccionGaleriaRepository extends MongoRepository<ColeccionGaleria, String> {
    Optional<ColeccionGaleria> findBySlug(String slug);
    List<ColeccionGaleria> findByEstado(String estado);
    List<ColeccionGaleria> findByEstadoOrderByOrdenAsc(String estado);
    List<ColeccionGaleria> findTop10ByOrderByOrdenAsc();
}
