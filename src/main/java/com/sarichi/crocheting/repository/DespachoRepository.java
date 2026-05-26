package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.Despacho;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends MongoRepository<Despacho, String> {
    Optional<Despacho> findByPedidoId(String pedidoId);
    List<Despacho> findByEstado(String estado);
    List<Despacho> findByTransportadora(String transportadora);
    List<Despacho> findByFechaDespachoBetween(LocalDateTime inicio, LocalDateTime fin);
}
