package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.FotoGaleria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FotoGaleriaRepository extends MongoRepository<FotoGaleria, String> {
    List<FotoGaleria> findByIdColeccionOrderByOrdenAsc(String idColeccion);
    List<FotoGaleria> findByIdProducto(String idProducto);
    List<FotoGaleria> findTop6ByOrderByFechaPublicacionDesc();
    List<FotoGaleria> findByIdColeccionAndFechaPublicacionBetween(String idColeccion, LocalDateTime desde, LocalDateTime hasta);
    Long countByIdColeccion(String idColeccion);
}
