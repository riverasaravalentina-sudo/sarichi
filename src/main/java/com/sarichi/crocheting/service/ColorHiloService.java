package com.sarichi.crocheting.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.ColorHiloDTO;
import com.sarichi.crocheting.entity.ColorHilo;
import com.sarichi.crocheting.repository.ColorHiloRepository;

/**
 * Servicio para operaciones sobre colores de hilo. Separa la lógica del controller.
 */
@Service
public class ColorHiloService {

    private static final Logger log = LoggerFactory.getLogger(ColorHiloService.class);

    @Autowired
    private ColorHiloRepository colorHiloRepository;

    @Autowired
    private MovimientoBodegaService movimientoBodegaService;

    // Listar todos los colores (público)
    public List<ColorHiloDTO> listarTodos() {
        log.info("Listando todos los colores de hilo");
        List<ColorHilo> lista = colorHiloRepository.findAll();
        return lista.stream().map(this::toDto).collect(Collectors.toList());
    }

    // Obtener por id
    public ColorHiloDTO obtenerPorId(String id) {
        log.info("Obteniendo color hilo por id: {}", id);
        ColorHilo ch = colorHiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + id));
        return toDto(ch);
    }

    // Obtener críticos comparando stockMetros <= stockMinimo
    public List<ColorHiloDTO> obtenerCriticos() {
        log.info("Obteniendo colores de hilo críticos");
        List<ColorHilo> criticos = colorHiloRepository.findCriticos();
        return criticos.stream().map(this::toDto).collect(Collectors.toList());
    }

    // Crear nuevo color
    public ColorHiloDTO crear(ColorHiloDTO dto) {
        log.info("Creando color de hilo: {}", dto.getNombre());
        ColorHilo entidad = ColorHilo.builder()
                .nombre(dto.getNombre())
                .codigoHex(dto.getCodigoHex())
                .descripcion(dto.getDescripcion())
                .stockMetros(dto.getStockMetros())
                .stockMinimo(dto.getStockMinimo())
                .proveedor(dto.getProveedor())
                .precioMetro(dto.getPrecioMetro())
                .imagenUrl(dto.getImagenUrl())
                .build();
        ColorHilo saved = colorHiloRepository.save(entidad);
        return toDto(saved);
    }

    // Actualizar existente
    public ColorHiloDTO actualizar(String id, ColorHiloDTO dto) {
        log.info("Actualizando color de hilo: {}", id);
        ColorHilo existente = colorHiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + id));

        existente.setNombre(dto.getNombre());
        existente.setCodigoHex(dto.getCodigoHex());
        existente.setDescripcion(dto.getDescripcion());
        existente.setStockMetros(dto.getStockMetros());
        existente.setStockMinimo(dto.getStockMinimo());
        existente.setProveedor(dto.getProveedor());
        existente.setPrecioMetro(dto.getPrecioMetro());
        existente.setImagenUrl(dto.getImagenUrl());

        ColorHilo updated = colorHiloRepository.save(existente);
        return toDto(updated);
    }

    // Eliminación lógica: prefix al nombre
    public void eliminar(String id) {
        log.info("Eliminando lógicamente color de hilo: {}", id);
        ColorHilo existente = colorHiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + id));
        existente.setNombre("[ELIMINADO] " + existente.getNombre());
        colorHiloRepository.save(existente);
    }

    public ColorHiloDTO registrarEntrada(String id, Double cantidad, String responsable, String observacion) {
        ColorHilo ch = colorHiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + id));
        Double anterior = ch.getStockMetros();
        ch.setStockMetros(anterior + cantidad);
        ColorHilo actualizado = colorHiloRepository.save(ch);
        movimientoBodegaService.registrar("ENTRADA", id, ch.getNombre(), "HILO",
                cantidad, anterior, ch.getStockMetros(), responsable, observacion);
        return toDto(actualizado);
    }

    public ColorHiloDTO registrarSalida(String id, Double cantidad, String responsable, String observacion) {
        ColorHilo ch = colorHiloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado: " + id));
        Double anterior = ch.getStockMetros();
        if (anterior < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + anterior + "m, solicitado: " + cantidad + "m");
        }
        ch.setStockMetros(anterior - cantidad);
        ColorHilo actualizado = colorHiloRepository.save(ch);
        movimientoBodegaService.registrar("SALIDA", id, ch.getNombre(), "HILO",
                cantidad, anterior, ch.getStockMetros(), responsable, observacion);
        return toDto(actualizado);
    }

    // Mapper entidad -> DTO
    private ColorHiloDTO toDto(ColorHilo c) {
        return ColorHiloDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .codigoHex(c.getCodigoHex())
                .descripcion(c.getDescripcion())
                .stockMetros(c.getStockMetros())
                .stockMinimo(c.getStockMinimo())
                .proveedor(c.getProveedor())
                .precioMetro(c.getPrecioMetro())
                .imagenUrl(c.getImagenUrl())
                .build();
    }
}
