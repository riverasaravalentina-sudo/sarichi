package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.DisenioGuardado;

@Repository
public interface DisenioGuardadoRepository extends MongoRepository<DisenioGuardado, String> {

    List<DisenioGuardado> findByUsuarioIdOrderByFechaCreacionDesc(String usuarioId);
    List<DisenioGuardado> findByEstadoOrderByFechaCreacionDesc(String estado);
    List<DisenioGuardado> findByEstadoNotOrderByFechaCreacionDesc(String estado);
}
