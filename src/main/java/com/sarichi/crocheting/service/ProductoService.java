package com.sarichi.crocheting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.ProductoDTO;
import com.sarichi.crocheting.dto.ProductoFiltroDTO;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.repository.ProductoRepository;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoBodegaService movimientoBodegaService;

    // ── CREAR ─────────────────────────────────────────────────────────────

    public ProductoDTO crear(ProductoDTO dto) {
        log.info("Creando producto: {}", dto.getNombre());

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precioBase(dto.getPrecioBase())
                .categoria(dto.getCategoria())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .coloresDisponibles(dto.getColoresDisponibles())
                .fotosUrls(dto.getFotosUrls())
                .tiempoElaboracionDias(dto.getTiempoElaboracionDias())
                .estado("ACTIVO")
                .build();

        return mapearADTO(productoRepository.save(producto));
    }

    // ── LISTAR CON FILTROS ────────────────────────────────────────────────

    public List<ProductoDTO> listarConFiltros(ProductoFiltroDTO filtro) {
        List<Producto> productos;

        // Búsqueda por texto libre
        if (filtro.getBusqueda() != null && !filtro.getBusqueda().isBlank()) {
            productos = productoRepository.buscarPorTexto(filtro.getBusqueda());

        // Filtro por categoría
        } else if (filtro.getCategoria() != null && !filtro.getCategoria().isBlank()) {
            productos = productoRepository
                    .findByCategoriaAndEstado(filtro.getCategoria(), "ACTIVO");

        // Sin filtros — todos los activos con stock
        } else {
            productos = productoRepository
                    .findByEstadoAndStockGreaterThan("ACTIVO", 0);
        }

        // Filtro por precio
        if (filtro.getPrecioMin() != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecioBase() >= filtro.getPrecioMin())
                    .collect(Collectors.toList());
        }
        if (filtro.getPrecioMax() != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecioBase() <= filtro.getPrecioMax())
                    .collect(Collectors.toList());
        }

        // Filtro por color
        if (filtro.getColorId() != null && !filtro.getColorId().isBlank()) {
            productos = productos.stream()
                    .filter(p -> p.getColoresDisponibles().contains(filtro.getColorId()))
                    .collect(Collectors.toList());
        }

        // Paginación simple
        int desde = filtro.getPagina() * filtro.getTamano();
        int hasta = Math.min(desde + filtro.getTamano(), productos.size());

        if (desde >= productos.size()) return List.of();

        return productos.subList(desde, hasta)
                .stream().map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    // ── OBTENER POR ID ────────────────────────────────────────────────────

    public ProductoDTO obtenerPorId(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        return mapearADTO(producto);
    }

    // ── LISTAR TODOS (ADMIN) ──────────────────────────────────────────────

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll()
                .stream().map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────

    public ProductoDTO actualizar(String id, ProductoDTO dto) {
        log.info("Actualizando producto: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setCategoria(dto.getCategoria());
        producto.setStock(dto.getStock());
        producto.setColoresDisponibles(dto.getColoresDisponibles());
        producto.setFotosUrls(dto.getFotosUrls());
        producto.setTiempoElaboracionDias(dto.getTiempoElaboracionDias());

        return mapearADTO(productoRepository.save(producto));
    }

    // ── ELIMINAR (lógico) ─────────────────────────────────────────────────

    public void eliminar(String id) {
        log.info("Eliminando producto: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        producto.setEstado("INACTIVO");
        productoRepository.save(producto);
    }

    // ── ENTRADA / SALIDA ──────────────────────────────────────────────────

    public ProductoDTO registrarEntrada(String id, int cantidad, String responsable, String observacion) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        int anterior = p.getStock();
        p.setStock(anterior + cantidad);
        Producto actualizado = productoRepository.save(p);
        movimientoBodegaService.registrar("ENTRADA", id, p.getNombre(), "PRODUCTO",
                (double) cantidad, (double) anterior, (double) p.getStock(), responsable, observacion);
        return mapearADTO(actualizado);
    }

    public ProductoDTO registrarSalida(String id, int cantidad, String responsable, String observacion) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        int anterior = p.getStock();
        if (anterior < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + anterior + ", solicitado: " + cantidad);
        }
        p.setStock(anterior - cantidad);
        Producto actualizado = productoRepository.save(p);
        movimientoBodegaService.registrar("SALIDA", id, p.getNombre(), "PRODUCTO",
                (double) cantidad, (double) anterior, (double) p.getStock(), responsable, observacion);
        return mapearADTO(actualizado);
    }

    // ── STOCK CRÍTICO ─────────────────────────────────────────────────────

    public List<ProductoDTO> obtenerStockCritico(int umbral) {
        return productoRepository
                .findByEstadoAndStockLessThanEqual("ACTIVO", umbral)
                .stream().map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    // ── MAPPER ────────────────────────────────────────────────────────────

    public ProductoDTO mapearADTO(Producto p) {
        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precioBase(p.getPrecioBase())
                .categoria(p.getCategoria())
                .stock(p.getStock())
                .coloresDisponibles(p.getColoresDisponibles())
                .fotosUrls(p.getFotosUrls())
                .tiempoElaboracionDias(p.getTiempoElaboracionDias())
                .estado(p.getEstado())
                .build();
    }
}