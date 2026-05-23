package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.ColorHilo;

@Repository
public interface ColorHiloRepository extends MongoRepository<ColorHilo, String> {

    List<ColorHilo> findByStockMetrosLessThanEqual(Double stockMinimo);

    @org.springframework.data.mongodb.repository.Query("{ $expr: { $lte: ['$stockMetros', '$stockMinimo'] } }")
    List<ColorHilo> findCriticos();
}