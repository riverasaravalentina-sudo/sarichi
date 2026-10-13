package com.sarichi.crocheting.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Pago;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
    Optional<Pago> findByReferencia(String referencia);
}
