package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.EstadoNumero;
import com.sarichi.crocheting.entity.RifaNumero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RifaNumeroRepository extends MongoRepository<RifaNumero, String> {

    List<RifaNumero> findByRifaId(String rifaId);

    List<RifaNumero> findByRifaIdAndEstado(String rifaId, EstadoNumero estado);

    Optional<RifaNumero> findByRifaIdAndNumero(String rifaId, Integer numero);

    List<RifaNumero> findByRifaIdAndNumeroIn(String rifaId, List<Integer> numeros);

    List<RifaNumero> findByRifaIdAndCodigoReserva(String rifaId, String codigoReserva);

    List<RifaNumero> findByRifaIdAndEstadoAndReservadoHastaBefore(
            String rifaId, EstadoNumero estado, LocalDateTime limite);
}
