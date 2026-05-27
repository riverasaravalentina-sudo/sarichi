package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.RetoMensualDTO;
import com.sarichi.crocheting.dto.ParticipacionRetoDTO;
import com.sarichi.crocheting.entity.RetoMensual;
import com.sarichi.crocheting.entity.ParticipacionReto;
import com.sarichi.crocheting.exception.RetoException;
import com.sarichi.crocheting.repository.RetoMensualRepository;
import com.sarichi.crocheting.repository.ParticipacionRetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetoService {
    
    private final RetoMensualRepository retoRepository;
    private final ParticipacionRetoRepository participacionRepository;
    
    public RetoMensualDTO crearReto(RetoMensualDTO dto) {
        RetoMensual reto = RetoMensual.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .patronUrl(dto.getPatronUrl())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .estado("ACTIVO")
                .premioDescripcion(dto.getPremioDescripcion())
                .fechaCreacion(LocalDateTime.now())
                .build();
        
        return mapToDTO(retoRepository.save(reto));
    }
    
    public List<RetoMensualDTO> listarRetosActivos() {
        LocalDateTime ahora = LocalDateTime.now();
        return retoRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(ahora, ahora)
                .stream()
                .map(r -> mapToDTO(r, participacionRepository.countByIdReto(r.getId())))
                .collect(Collectors.toList());
    }
    
    public List<RetoMensualDTO> listarRetosTodos() {
        return retoRepository.findAll()
                .stream()
                .map(r -> mapToDTO(r, participacionRepository.countByIdReto(r.getId())))
                .collect(Collectors.toList());
    }
    
    public ParticipacionRetoDTO participarEnReto(String retoId, String usuarioId, String nombreUsuario, String fotoUrl, String descripcion) {
        RetoMensual reto = retoRepository.findById(retoId)
                .orElseThrow(() -> new RetoException("Reto no encontrado"));
        
        if (!esRetoActivo(reto)) {
            throw new RetoException("El reto no está activo");
        }
        
        if (participacionRepository.findByIdRetoAndIdUsuario(retoId, usuarioId).isPresent()) {
            throw new RetoException("Ya has participado en este reto");
        }
        
        ParticipacionReto participacion = ParticipacionReto.builder()
                .idReto(retoId)
                .idUsuario(usuarioId)
                .nombreUsuario(nombreUsuario)
                .urlFotoParticipacion(fotoUrl)
                .descripcion(descripcion)
                .fechaParticipacion(LocalDateTime.now())
                .votos(0L)
                .estado("APROBADA")
                .build();
        
        return mapParticipacionToDTO(participacionRepository.save(participacion));
    }
    
    public List<ParticipacionRetoDTO> listarParticipaciones(String retoId) {
        return participacionRepository.findByIdRetoAndEstado(retoId, "APROBADA")
                .stream()
                .map(this::mapParticipacionToDTO)
                .collect(Collectors.toList());
    }
    
    public ParticipacionRetoDTO votarParticipacion(String participacionId, String usuarioId) {
        ParticipacionReto participacion = participacionRepository.findById(participacionId)
                .orElseThrow(() -> new RetoException("Participación no encontrada"));
        
        participacion.setVotos(participacion.getVotos() + 1);
        return mapParticipacionToDTO(participacionRepository.save(participacion));
    }
    
    public RetoMensualDTO finalizarReto(String retoId, String ganadorId) {
        RetoMensual reto = retoRepository.findById(retoId)
                .orElseThrow(() -> new RetoException("Reto no encontrado"));
        
        reto.setEstado("FINALIZADO");
        reto.setGanadorId(ganadorId);
        
        // Marcar participación como ganadora
        ParticipacionReto ganador = participacionRepository.findById(ganadorId)
                .orElseThrow(() -> new RetoException("Participación ganadora no encontrada"));
        ganador.setEstado("GANADORA");
        participacionRepository.save(ganador);
        
        return mapToDTO(retoRepository.save(reto));
    }
    
    private boolean esRetoActivo(RetoMensual reto) {
        LocalDateTime ahora = LocalDateTime.now();
        return "ACTIVO".equals(reto.getEstado()) &&
               !ahora.isBefore(reto.getFechaInicio()) &&
               !ahora.isAfter(reto.getFechaFin());
    }
    
    private RetoMensualDTO mapToDTO(RetoMensual reto) {
        return mapToDTO(reto, 0L);
    }
    
    private RetoMensualDTO mapToDTO(RetoMensual reto, Long participacionesCount) {
        return RetoMensualDTO.builder()
                .id(reto.getId())
                .nombre(reto.getNombre())
                .descripcion(reto.getDescripcion())
                .patronUrl(reto.getPatronUrl())
                .fechaInicio(reto.getFechaInicio())
                .fechaFin(reto.getFechaFin())
                .estado(reto.getEstado())
                .premioDescripcion(reto.getPremioDescripcion())
                .ganadorId(reto.getGanadorId())
                .participacionesCount(participacionesCount)
                .build();
    }
    
    private ParticipacionRetoDTO mapParticipacionToDTO(ParticipacionReto p) {
        return ParticipacionRetoDTO.builder()
                .id(p.getId())
                .idReto(p.getIdReto())
                .idUsuario(p.getIdUsuario())
                .nombreUsuario(p.getNombreUsuario())
                .urlFotoParticipacion(p.getUrlFotoParticipacion())
                .descripcion(p.getDescripcion())
                .fechaParticipacion(p.getFechaParticipacion())
                .votos(p.getVotos())
                .estado(p.getEstado())
                .build();
    }
}
