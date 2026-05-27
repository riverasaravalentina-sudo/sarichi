package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.EventoTrafico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoTraficoRepository extends MongoRepository<EventoTrafico, String> {
    Long countByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    Long countByFuenteAndFechaBetween(String fuente, LocalDateTime desde, LocalDateTime hasta);
    List<EventoTrafico> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    Long countByTipoAndFechaBetween(String tipo, LocalDateTime desde, LocalDateTime hasta);
    List<EventoTrafico> findBySessionId(String sessionId);
}
