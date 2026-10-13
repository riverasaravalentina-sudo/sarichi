package com.sarichi.crocheting.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Configuracion;

/**
 * Repositorio para Configuracion. Solo existe una configuración por aplicación.
 */
@Repository
public interface ConfiguracionRepository extends MongoRepository<Configuracion, String> {

    Optional<Configuracion> findFirstBy();
}
