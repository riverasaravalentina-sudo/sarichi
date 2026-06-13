package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.MovimientoBodegaDTO;
import com.sarichi.crocheting.entity.MovimientoBodega;
import com.sarichi.crocheting.repository.MovimientoBodegaRepository;

@Service
public class MovimientoBodegaService {

    private final MovimientoBodegaRepository repository;

    public MovimientoBodegaService(MovimientoBodegaRepository repository) {
        this.repository = repository;
    }

    public List<MovimientoBodegaDTO> listarTodos() {
        return repository.findAllByOrderByFechaDesc().stream()
                .map(this::toDTO)
                .toList();
    }

    public MovimientoBodegaDTO registrar(String tipo, String itemId, String itemNombre,
                                          String itemTipo, Double cantidad,
                                          Double stockAnterior, Double stockPosterior,
                                          String responsable, String observacion) {
        MovimientoBodega m = MovimientoBodega.builder()
                .fecha(LocalDateTime.now())
                .tipo(tipo)
                .itemId(itemId)
                .itemNombre(itemNombre)
                .itemTipo(itemTipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockPosterior(stockPosterior)
                .responsable(responsable)
                .observacion(observacion)
                .build();
        return toDTO(repository.save(m));
    }

    private MovimientoBodegaDTO toDTO(MovimientoBodega m) {
        return MovimientoBodegaDTO.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .tipo(m.getTipo())
                .itemId(m.getItemId())
                .itemNombre(m.getItemNombre())
                .itemTipo(m.getItemTipo())
                .cantidad(m.getCantidad())
                .stockAnterior(m.getStockAnterior())
                .stockPosterior(m.getStockPosterior())
                .responsable(m.getResponsable())
                .observacion(m.getObservacion())
                .build();
    }
}
