package com.sarichi.crocheting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.DashboardKpisDTO;
import com.sarichi.crocheting.repository.ColorHiloRepository;
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

    /**
     * KPIs generales del dashboard.
     * Las ventas reales se conectarán en Sprint 3 con la colección 'pagos'.
     * Por ahora retorna datos reales de productos y usuarios.
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

        // Hilos con stock crítico — ahora comparamos stockMetros <= stockMinimo por documento
        long hilosCriticos = colorHiloRepository.findCriticos().size();

        return DashboardKpisDTO.builder()
                // Ventas simuladas hasta Sprint 3 (colección 'pagos')
                .ventasHoy(0.0)
                .ventasAyer(0.0)
                .variacionVentas(0.0)
                // Pedidos simulados hasta Sprint 4
                .pedidosPendientes(0L)
                .pedidosEnProduccion(0L)
                .productosParaDespachar(0L)
                // Datos reales desde MongoDB
                .stockCritico(stockCritico + hilosCriticos)
                .totalClientes(totalClientes)
                .totalProductos(totalProductos)
                .build();
    }
}