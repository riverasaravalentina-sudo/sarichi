package com.sarichi.crocheting.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.PersonalizacionDTO;
import com.sarichi.crocheting.entity.DisenioGuardado;
import com.sarichi.crocheting.repository.DisenioGuardadoRepository;

@Service
public class PersonalizadorService {

    private final DisenioGuardadoRepository disenioGuardadoRepository;
    private final ProductoService productoService;

    public PersonalizadorService(DisenioGuardadoRepository disenioGuardadoRepository,
                                  ProductoService productoService) {
        this.disenioGuardadoRepository = disenioGuardadoRepository;
        this.productoService = productoService;
    }

    public PersonalizacionDTO guardar(PersonalizacionDTO dto, String usuarioId) {
        var producto = productoService.obtenerPorId(dto.getProductoId());
        double precioBase = producto.getPrecioBase() != null ? producto.getPrecioBase() : 0;
        double precioCalculado = calcularPrecio(precioBase, dto.getTalla(), dto.getMensajeBordado());

        DisenioGuardado disenio = DisenioGuardado.builder()
                .usuarioId(usuarioId)
                .productoId(dto.getProductoId())
                .productoNombre(producto.getNombre())
                .coloresSeleccionados(dto.getColoresSeleccionados())
                .talla(dto.getTalla())
                .mensajeBordado(dto.getMensajeBordado())
                .precioCalculado(precioCalculado)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return toDTO(disenioGuardadoRepository.save(disenio));
    }

    public List<PersonalizacionDTO> listarPorUsuario(String usuarioId) {
        return disenioGuardadoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PersonalizacionDTO obtenerPorId(String id) {
        return disenioGuardadoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Diseño no encontrado"));
    }

    private double calcularPrecio(double precioBase, String talla, String mensaje) {
        double precio = precioBase;
        if ("Grande".equalsIgnoreCase(talla)) precio += 15000;
        if ("Extra Grande".equalsIgnoreCase(talla)) precio += 30000;
        if (mensaje != null && !mensaje.isBlank()) precio += 5000;
        return precio;
    }

    private PersonalizacionDTO toDTO(DisenioGuardado d) {
        return PersonalizacionDTO.builder()
                .id(d.getId())
                .productoId(d.getProductoId())
                .productoNombre(d.getProductoNombre())
                .coloresSeleccionados(d.getColoresSeleccionados())
                .talla(d.getTalla())
                .mensajeBordado(d.getMensajeBordado())
                .precioCalculado(d.getPrecioCalculado())
                .fechaCreacion(d.getFechaCreacion())
                .build();
    }
}
