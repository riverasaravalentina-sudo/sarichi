package com.sarichi.crocheting.config;

import com.sarichi.crocheting.entity.*;
import com.sarichi.crocheting.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ColorHiloRepository colorHiloRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DespachoRepository despachoRepository;
    @Autowired private ActividadProduccionRepository actividadRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByCorreo("logistica@sarichi.com")) {
            log.info("Seed data already exists, skipping.");
            return;
        }
        log.info("Seeding data for LOGISTICA role...");

        // ── Colores de hilo ───────────────────────────────────────────
        var colorBlanco = colorHiloRepository.save(ColorHilo.builder()
                .id("color-blanco").nombre("Blanco").codigoHex("#FFFFFF")
                .stockMetros(500.0).stockMinimo(50.0).proveedor("Hilos del Sur")
                .precioMetro(0.5).build());
        var colorAzul = colorHiloRepository.save(ColorHilo.builder()
                .id("color-azul").nombre("Azul Marino").codigoHex("#1A237E")
                .stockMetros(300.0).stockMinimo(30.0).proveedor("Hilos del Sur")
                .precioMetro(0.6).build());
        var colorRosa = colorHiloRepository.save(ColorHilo.builder()
                .id("color-rosa").nombre("Rosa Pastel").codigoHex("#F48FB1")
                .stockMetros(400.0).stockMinimo(40.0).proveedor("Textiles Colombia")
                .precioMetro(0.55).build());
        var colorVerde = colorHiloRepository.save(ColorHilo.builder()
                .id("color-verde").nombre("Verde Esmeralda").codigoHex("#2E7D32")
                .stockMetros(200.0).stockMinimo(20.0).proveedor("Textiles Colombia")
                .precioMetro(0.65).build());

        // ── Productos ─────────────────────────────────────────────────
        var prodConejo = productoRepository.save(Producto.builder()
                .id("producto-conejo").nombre("Amigurumi Conejo")
                .descripcion("Conejo tejido a mano en algodón hipoalergénico")
                .precioBase(35000.0).categoria("Amigurumis").stock(10)
                .coloresDisponibles(List.of(colorBlanco.getId(), colorRosa.getId()))
                .tiempoElaboracionDias(3).build());
        var prodBufanda = productoRepository.save(Producto.builder()
                .id("producto-bufanda").nombre("Bufanda Tejida")
                .descripcion("Bufanda larga de lana merino, diseño clásico")
                .precioBase(25000.0).categoria("Accesorios").stock(15)
                .coloresDisponibles(List.of(colorAzul.getId(), colorVerde.getId()))
                .tiempoElaboracionDias(2).build());
        var prodCobija = productoRepository.save(Producto.builder()
                .id("producto-cobija").nombre("Cobija Patchwork")
                .descripcion("Cobija multicolor elaborada en técnica patchwork")
                .precioBase(80000.0).categoria("Hogar").stock(5)
                .coloresDisponibles(List.of(colorBlanco.getId(), colorAzul.getId(), colorRosa.getId()))
                .tiempoElaboracionDias(7).build());
        var prodGorro = productoRepository.save(Producto.builder()
                .id("producto-gorro").nombre("Gorro Navideño")
                .descripcion("Gorro navideño tejido con hilo brillante")
                .precioBase(15000.0).categoria("Accesorios").stock(20)
                .coloresDisponibles(List.of(colorRosa.getId(), colorVerde.getId()))
                .tiempoElaboracionDias(1).build());
        var prodOso = productoRepository.save(Producto.builder()
                .id("producto-oso").nombre("Amigurumi Oso")
                .descripcion("Osito de peluche tejido a mano, 25 cm")
                .precioBase(40000.0).categoria("Amigurumis").stock(8)
                .coloresDisponibles(List.of(colorBlanco.getId(), colorAzul.getId()))
                .tiempoElaboracionDias(4).build());

        // ── Usuarios ──────────────────────────────────────────────────
        var pwd = passwordEncoder.encode("123456");

        var logistica = usuarioRepository.save(Usuario.builder()
                .id("usr-logistica").nombre("María Logística")
                .correo("logistica@sarichi.com").passwordHash(pwd)
                .telefono("3001112222").rol(UserRole.LOGISTICA).build());

        var artesana = usuarioRepository.save(Usuario.builder()
                .id("usr-artesana").nombre("Sofía Artesana")
                .correo("artesana@sarichi.com").passwordHash(pwd)
                .telefono("3003334444").rol(UserRole.ARTESANA).build());

        var admin = usuarioRepository.save(Usuario.builder()
                .id("usr-admin").nombre("Admin Sarichi")
                .correo("admin@sarichi.com").passwordHash(pwd)
                .telefono("3005556666").rol(UserRole.ADMIN).build());

        var cli1 = usuarioRepository.save(Usuario.builder()
                .id("usr-cliente1").nombre("Ana Martínez")
                .correo("ana@email.com").passwordHash(pwd)
                .telefono("3007778888").rol(UserRole.CLIENTE)
                .direcciones(List.of("Calle 45 #12-34, Bogotá"))
                .build());

        var cli2 = usuarioRepository.save(Usuario.builder()
                .id("usr-cliente2").nombre("Carlos López")
                .correo("carlos@email.com").passwordHash(pwd)
                .telefono("3009990000").rol(UserRole.CLIENTE)
                .direcciones(List.of("Cra 7 #89-01, Medellín"))
                .build());

        // ── Pedidos (cadena completa de estados) ──────────────────────
        var ahora = LocalDateTime.now();
        var ayer = ahora.minusDays(1);
        var anteayer = ahora.minusDays(2);
        var hace3d = ahora.minusDays(3);
        var hace5d = ahora.minusDays(5);

        // Pedido 1 — SOLICITADO (apenas creado)
        pedidoRepository.save(Pedido.builder()
                .id("pedido-solicitado").usuarioId(cli1.getId())
                .fechaPedido(hace3d).total(35000.0).estado("SOLICITADO")
                .direccionEnvio("Calle 45 #12-34, Bogotá")
                .items(List.of(ItemPedido.builder()
                        .productoId(prodConejo.getId()).nombreProducto(prodConejo.getNombre())
                        .cantidad(1).colorSolicitado("Blanco")
                        .precioUnitario(35000.0).subtotal(35000.0)
                        .build()))
                .build());

        // Pedido 2 — EN_PRODUCCION (artesana trabajando)
        pedidoRepository.save(Pedido.builder()
                .id("pedido-produccion").usuarioId(cli2.getId())
                .fechaPedido(anteayer).total(25000.0).estado("EN_PRODUCCION")
                .direccionEnvio("Cra 7 #89-01, Medellín")
                .items(List.of(ItemPedido.builder()
                        .productoId(prodBufanda.getId()).nombreProducto(prodBufanda.getNombre())
                        .cantidad(1).colorSolicitado("Azul Marino")
                        .precioUnitario(25000.0).subtotal(25000.0)
                        .build()))
                .build());

        // Pedido 3 — LISTO (pendiente de despacho → lo ve LOGISTICA)
        pedidoRepository.save(Pedido.builder()
                .id("pedido-listo").usuarioId(cli1.getId())
                .fechaPedido(ayer).total(80000.0).estado("LISTO")
                .direccionEnvio("Calle 45 #12-34, Bogotá")
                .items(List.of(ItemPedido.builder()
                        .productoId(prodCobija.getId()).nombreProducto(prodCobija.getNombre())
                        .cantidad(1).colorSolicitado("Blanco")
                        .precioUnitario(80000.0).subtotal(80000.0)
                        .build()))
                .build());

        // Pedido 4 — DESPACHADO (ya tiene guía, en tránsito)
        pedidoRepository.save(Pedido.builder()
                .id("pedido-despachado").usuarioId(cli2.getId())
                .fechaPedido(hace3d).total(55000.0).estado("DESPACHADO")
                .direccionEnvio("Cra 7 #89-01, Medellín")
                .items(List.of(
                        ItemPedido.builder().productoId(prodGorro.getId())
                                .nombreProducto(prodGorro.getNombre()).cantidad(1)
                                .colorSolicitado("Rosa Pastel").precioUnitario(15000.0)
                                .subtotal(15000.0).build(),
                        ItemPedido.builder().productoId(prodOso.getId())
                                .nombreProducto(prodOso.getNombre()).cantidad(1)
                                .colorSolicitado("Blanco").precioUnitario(40000.0)
                                .subtotal(40000.0).build()))
                .build());

        // Pedido 5 — ENTREGADO (completado)
        pedidoRepository.save(Pedido.builder()
                .id("pedido-entregado").usuarioId(cli1.getId())
                .fechaPedido(hace5d).total(75000.0).estado("ENTREGADO")
                .fechaEntregaEstimada(hace3d)
                .direccionEnvio("Calle 45 #12-34, Bogotá")
                .items(List.of(
                        ItemPedido.builder().productoId(prodConejo.getId())
                                .nombreProducto(prodConejo.getNombre()).cantidad(1)
                                .colorSolicitado("Blanco").precioUnitario(35000.0)
                                .subtotal(35000.0).build(),
                        ItemPedido.builder().productoId(prodOso.getId())
                                .nombreProducto(prodOso.getNombre()).cantidad(1)
                                .colorSolicitado("Azul Marino").precioUnitario(40000.0)
                                .subtotal(40000.0).build()))
                .build());

        // ── Actividades de producción ───────────────────────────────────
        actividadRepository.save(ActividadProduccion.builder()
                .id("actividad-produccion").pedidoId("pedido-produccion")
                .productoId(prodBufanda.getId()).artesanaId(artesana.getId())
                .cantidad(1).fechaInicio(ayer).estado("EN_PROGRESO")
                .notas("Tejiendo bufanda en punto inglés").build());

        actividadRepository.save(ActividadProduccion.builder()
                .id("actividad-listo").pedidoId("pedido-listo")
                .productoId(prodCobija.getId()).artesanaId(artesana.getId())
                .cantidad(1).fechaInicio(anteayer)
                .fechaFinalizacion(ayer).estado("COMPLETADO")
                .notas("Cobija patchwork terminada").build());

        actividadRepository.save(ActividadProduccion.builder()
                .id("actividad-despachado").pedidoId("pedido-despachado")
                .productoId(prodGorro.getId()).artesanaId(artesana.getId())
                .cantidad(1).fechaInicio(hace3d)
                .fechaFinalizacion(anteayer).estado("COMPLETADO")
                .notas("Gorro navideño terminado").build());

        actividadRepository.save(ActividadProduccion.builder()
                .id("actividad-entregado").pedidoId("pedido-entregado")
                .productoId(prodConejo.getId()).artesanaId(artesana.getId())
                .cantidad(1).fechaInicio(hace5d)
                .fechaFinalizacion(hace3d).estado("COMPLETADO")
                .notas("Conejo y oso terminados").build());

        // ── Despachos (para pedidos DESPACHADO y ENTREGADO) ─────────────
        despachoRepository.save(Despacho.builder()
                .id("despacho-transito").pedidoId("pedido-despachado")
                .fechaDespacho(ayer).transportadora("Coordinadora")
                .numeroGuia("GU-2026001").estado("EN_TRANSITO")
                .fechaEstimadaEntrega(ahora.plusDays(2))
                .observaciones("Entregar en horario laboral").build());

        despachoRepository.save(Despacho.builder()
                .id("despacho-entregado").pedidoId("pedido-entregado")
                .fechaDespacho(hace3d).transportadora("Interrapidísimo")
                .numeroGuia("GU-2026002").estado("ENTREGADO")
                .fechaEstimadaEntrega(ayer)
                .observaciones("Entregado a satisfacción").build());

        log.info("Seed data created successfully. Users (all pwd=123456):");
        log.info("  LOGISTICA → logistica@sarichi.com");
        log.info("  ARTESANA  → artesana@sarichi.com");
        log.info("  ADMIN     → admin@sarichi.com");
        log.info("  CLIENTE   → ana@email.com / carlos@email.com");
    }
}
