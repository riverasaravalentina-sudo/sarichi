package com.sarichi.crocheting.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.Mensaje;

@Repository
public interface MensajeRepository extends MongoRepository<Mensaje, String> {

    List<Mensaje> findByPedidoIdOrderByTimestampAsc(String pedidoId);

    long countByPedidoIdAndLeidoFalse(String pedidoId);

    List<Mensaje> findByPedidoIdAndRemitenteIdNotAndLeidoFalse(String pedidoId, String remitenteId);
}
