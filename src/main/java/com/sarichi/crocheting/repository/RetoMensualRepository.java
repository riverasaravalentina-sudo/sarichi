package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.RetoMensual;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RetoMensualRepository extends MongoRepository<RetoMensual, String> {
    List<RetoMensual> findByEstado(String estado);
    List<RetoMensual> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(LocalDateTime ahora1, LocalDateTime ahora2);
    List<RetoMensual> findByEstadoOrderByFechaInicioDesc(String estado);
}
