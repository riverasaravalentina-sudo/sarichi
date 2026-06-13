package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.MovimientoBodega;

@Repository
public interface MovimientoBodegaRepository extends MongoRepository<MovimientoBodega, String> {

    List<MovimientoBodega> findAllByOrderByFechaDesc();
}
