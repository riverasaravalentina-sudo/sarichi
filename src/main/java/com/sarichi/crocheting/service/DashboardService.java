package com.sarichi.crocheting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.DashboardKpisDTO;
import com.sarichi.crocheting.dto.VentasPorCategoriaDTO;
import com.sarichi.crocheting.dto.VentasPorPeriodoDTO;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.repository.ColorHiloRepository;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ColorHiloRepository colorHiloRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * KPIs generales del dashboard.
     * Incluye datos reales de pedidos desde MongoDB.
     */
    public DashboardKpisDTO obtenerKpis() {
        log.info("Calculando KPIs del dashboard");

        // Stock crítico — productos con stock <= 2
        long stockCritico = productoRepository
                .countByEstadoAndStockLessThanEqual("ACTIVO", 2);

        // Total clientes
        long totalClientes = usuarioRepository.countByRol(
                com.sarichi.crocheting.entity.UserRole.CLIENTE);

        // Total productos activos
        long totalProductos = productoRepository
                .findByEstado("ACTIVO").size();

        // Hilos con stock crítico
        long hilosCriticos = colorHiloRepository.findCriticos().size();

        // Pedidos por estado
        long pedidosPendientes = pedidoRepository.countByEstado("PENDIENTE");
        long pedidosEnProduccion = pedidoRepository.countByEstado("EN_PRODUCCION");
        long productosParaDespachar = pedidoRepository.countByEstado("LISTO");

        // Ventas del día
        Double ventasHoy = obtenerVentasDelDia();

        return DashboardKpisDTO.builder()
                .ventasHoy(ventasHoy)
                .ventasAyer(0.0)
                .variacionVentas(0.0)
                .pedidosPendientes(pedidosPendientes)
                .pedidosEnProduccion(pedidosEnProduccion)
                .productosParaDespachar(productosParaDespachar)
                .stockCritico(stockCritico + hilosCriticos)
                .totalClientes(totalClientes)
                .totalProductos(totalProductos)
                .build();
    }

    public Double obtenerVentasDelDia() {
        LocalDateTime hoy = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime mañana = hoy.plusDays(1);
        
        List<Pedido> pedidosHoy = pedidoRepository.findByFechaPedidoBetween(hoy, mañana);
        return pedidosHoy.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();
    }

    public List<VentasPorPeriodoDTO> obtenerVentasPorPeriodo(LocalDateTime desde, LocalDateTime hasta) {
        List<Pedido> pedidos = pedidoRepository.findByFechaPedidoBetween(desde, hasta);
        
        Map<LocalDate, Double> ventasPorFecha = new HashMap<>();
        for (Pedido pedido : pedidos) {
            LocalDate fecha = pedido.getFechaPedido().toLocalDate();
            ventasPorFecha.put(fecha, ventasPorFecha.getOrDefault(fecha, 0.0) + pedido.getTotal());
        }

        return ventasPorFecha.entrySet().stream()
                .map(e -> {
                    LocalDate fecha = e.getKey();
                    long count = pedidos.stream()
                            .filter(p -> p.getFechaPedido().toLocalDate().equals(fecha))
                            .count();
                    return VentasPorPeriodoDTO.builder()
                            .fecha(fecha)
                            .total(e.getValue())
                            .cantidadPedidos(count)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<VentasPorCategoriaDTO> obtenerVentasPorCategoria() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        
        Map<String, Double> ventasPorCategoria = new HashMap<>();
        Double totalVentas = 0.0;

        for (Pedido pedido : pedidos) {
            for (var item : pedido.getItems()) {
                Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);
                if (producto != null) {
                    String categoria = producto.getCategoria();
                    ventasPorCategoria.put(categoria, 
                            ventasPorCategoria.getOrDefault(categoria, 0.0) + item.getSubtotal());
                    totalVentas += item.getSubtotal();
                }
            }
        }

        Double finalTotalVentas = totalVentas;
        return ventasPorCategoria.entrySet().stream()
                .map(e -> VentasPorCategoriaDTO.builder()
                        .categoria(e.getKey())
                        .total(e.getValue())
                        .porcentaje(finalTotalVentas > 0 ? (e.getValue() / finalTotalVentas) * 100 : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> obtenerTopProductos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        
        Map<String, Long> ventasPorProducto = new HashMap<>();
        Map<String, Double> ingresosPorProducto = new HashMap<>();

        for (Pedido pedido : pedidos) {
            for (var item : pedido.getItems()) {
                ventasPorProducto.put(item.getProductoId(), 
                        ventasPorProducto.getOrDefault(item.getProductoId(), 0L) + item.getCantidad());
                ingresosPorProducto.put(item.getProductoId(),
                        ingresosPorProducto.getOrDefault(item.getProductoId(), 0.0) + item.getSubtotal());
            }
        }

        return ventasPorProducto.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Producto producto = productoRepository.findById(e.getKey()).orElse(null);
                    Map<String, Object> result = new HashMap<>();
                    result.put("productoId", e.getKey());
                    result.put("nombreProducto", producto != null ? producto.getNombre() : "Desconocido");
                    result.put("cantidadVendida", e.getValue());
                    result.put("ingresos", ingresosPorProducto.get(e.getKey()));
                    return result;
                })
                .collect(Collectors.toList());
    }
}