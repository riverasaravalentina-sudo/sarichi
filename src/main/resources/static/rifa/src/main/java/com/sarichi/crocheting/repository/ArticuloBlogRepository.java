package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.ArticuloBlog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloBlogRepository extends MongoRepository<ArticuloBlog, String> {
    Optional<ArticuloBlog> findBySlug(String slug);
    List<ArticuloBlog> findByEstado(String estado);
    List<ArticuloBlog> findByEstadoAndFechaPublicacionBetween(String estado, LocalDateTime desde, LocalDateTime hasta);
    List<ArticuloBlog> findByCategoriasContaining(String categoria);
    List<ArticuloBlog> findTop5ByEstadoOrderByVisitasDesc(String estado);
    Long countByEstado(String estado);
    List<ArticuloBlog> findByFechaPublicacionBetween(LocalDateTime desde, LocalDateTime hasta);
    List<ArticuloBlog> findByEstadoOrderByFechaPublicacionDesc(String estado);
}
