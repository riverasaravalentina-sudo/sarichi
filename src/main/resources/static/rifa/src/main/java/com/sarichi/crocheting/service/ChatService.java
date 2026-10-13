package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.MensajeDTO;
import com.sarichi.crocheting.entity.Mensaje;
import com.sarichi.crocheting.repository.MensajeRepository;

@Service
public class ChatService {

    private final MensajeRepository mensajeRepository;

    public ChatService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public MensajeDTO enviarMensaje(String pedidoId, String remitenteId,
                                    String remitenteNombre, String remitenteRol,
                                    String contenido) {
        Mensaje mensaje = Mensaje.builder()
                .pedidoId(pedidoId)
                .remitenteId(remitenteId)
                .remitenteNombre(remitenteNombre)
                .remitenteRol(remitenteRol)
                .contenido(contenido)
                .timestamp(LocalDateTime.now())
                .leido(false)
                .build();

        Mensaje guardado = mensajeRepository.save(mensaje);
        return toDTO(guardado);
    }

    public List<MensajeDTO> obtenerMensajes(String pedidoId) {
        return mensajeRepository.findByPedidoIdOrderByTimestampAsc(pedidoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void marcarComoLeidos(String pedidoId, String remitenteId) {
        List<Mensaje> noLeidos = mensajeRepository
                .findByPedidoIdAndRemitenteIdNotAndLeidoFalse(pedidoId, remitenteId);
        noLeidos.forEach(m -> m.setLeido(true));
        mensajeRepository.saveAll(noLeidos);
    }

    public long contarNoLeidos(String pedidoId, String remitenteId) {
        return mensajeRepository
                .findByPedidoIdAndRemitenteIdNotAndLeidoFalse(pedidoId, remitenteId)
                .size();
    }

    private MensajeDTO toDTO(Mensaje m) {
        return MensajeDTO.builder()
                .id(m.getId())
                .pedidoId(m.getPedidoId())
                .remitenteId(m.getRemitenteId())
                .remitenteNombre(m.getRemitenteNombre())
                .remitenteRol(m.getRemitenteRol())
                .contenido(m.getContenido())
                .timestamp(m.getTimestamp())
                .leido(m.isLeido())
                .build();
    }
}
