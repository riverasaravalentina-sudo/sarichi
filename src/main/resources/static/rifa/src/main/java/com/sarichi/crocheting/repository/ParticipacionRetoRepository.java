package com.sarichi.crocheting.repository;

import com.sarichi.crocheting.entity.ParticipacionReto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipacionRetoRepository extends MongoRepository<ParticipacionReto, String> {
    List<ParticipacionReto> findByIdRetoOrderByVotosDesc(String idReto);
    Optional<ParticipacionReto> findByIdRetoAndIdUsuario(String idReto, String idUsuario);
    Long countByIdReto(String idReto);
    List<ParticipacionReto> findTop3ByIdRetoOrderByVotosDesc(String idReto);
    List<ParticipacionReto> findByIdRetoAndEstado(String idReto, String estado);
}
