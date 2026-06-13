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
        var producto = dto.getProductoId() != null ? productoService.obtenerPorId(dto.getProductoId()) : null;
        double precioBase = producto != null && producto.getPrecioBase() != null ? producto.getPrecioBase() : 0;
        double precioCalculado = calcularPrecio(precioBase, dto.getTalla(), dto.getMensajeBordado());

        DisenioGuardado disenio = DisenioGuardado.builder()
                .usuarioId(usuarioId)
                .productoId(dto.getProductoId())
                .productoNombre(producto != null ? producto.getNombre() : null)
                .descripcion(dto.getDescripcion())
                .coloresSeleccionados(dto.getColoresSeleccionados())
                .talla(dto.getTalla())
                .mensajeBordado(dto.getMensajeBordado())
                .precioCalculado(dto.getProductoId() != null ? precioCalculado : null)
                .fechaCreacion(LocalDateTime.now())
                .estado("PENDIENTE")
                .build();

        return toDTO(disenioGuardadoRepository.save(disenio));
    }

    public List<PersonalizacionDTO> listarPorUsuario(String usuarioId) {
        return disenioGuardadoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PersonalizacionDTO> listarPendientes() {
        return disenioGuardadoRepository.findByEstadoOrderByFechaCreacionDesc("PENDIENTE")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PersonalizacionDTO> listarCotizaciones() {
        return disenioGuardadoRepository.findByEstadoNotOrderByFechaCreacionDesc("PENDIENTE")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PersonalizacionDTO obtenerPorId(String id) {
        return disenioGuardadoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Diseño no encontrado"));
    }

    public PersonalizacionDTO cotizar(String id, Double precioCotizacion, String tiempoEstimado) {
        DisenioGuardado disenio = disenioGuardadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diseño no encontrado"));
        disenio.setPrecioCotizacion(precioCotizacion);
        disenio.setTiempoEstimado(tiempoEstimado);
        disenio.setEstado("COTIZADO");
        return toDTO(disenioGuardadoRepository.save(disenio));
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
                .descripcion(d.getDescripcion())
                .coloresSeleccionados(d.getColoresSeleccionados())
                .talla(d.getTalla())
                .mensajeBordado(d.getMensajeBordado())
                .precioCalculado(d.getPrecioCalculado())
                .fechaCreacion(d.getFechaCreacion())
                .estado(d.getEstado())
                .precioCotizacion(d.getPrecioCotizacion())
                .tiempoEstimado(d.getTiempoEstimado())
                .build();
    }
}
