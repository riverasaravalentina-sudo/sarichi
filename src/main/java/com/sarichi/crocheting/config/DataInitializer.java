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
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ColorHiloRepository colorHiloRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DespachoRepository despachoRepository;
    @Autowired private ActividadProduccionRepository actividadRepository;
    @Autowired(required = false) private EventoTraficoRepository eventoTraficoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByCorreo("mercadeo@sarichi.com")) {
            log.info("Seed data already exists, skipping.");
            return;
        }
        log.info("=== Seeding full demo data ===");

        var pwd = passwordEncoder.encode("123456");
        var ahora = LocalDateTime.now();

        // ── Colores de hilo (12) ───────────────────────────────────────
        var cBlanco   = colorHiloRepository.save(ColorHilo.builder().id("c-blanco").nombre("Blanco").codigoHex("#FFFFFF").stockMetros(500.0).stockMinimo(50.0).proveedor("Hilos del Sur").precioMetro(0.5).build());
        var cAzul     = colorHiloRepository.save(ColorHilo.builder().id("c-azul").nombre("Azul Marino").codigoHex("#1A237E").stockMetros(300.0).stockMinimo(30.0).proveedor("Hilos del Sur").precioMetro(0.6).build());
        var cRosa     = colorHiloRepository.save(ColorHilo.builder().id("c-rosa").nombre("Rosa Pastel").codigoHex("#F48FB1").stockMetros(400.0).stockMinimo(40.0).proveedor("Textiles Colombia").precioMetro(0.55).build());
        var cVerde    = colorHiloRepository.save(ColorHilo.builder().id("c-verde").nombre("Verde Esmeralda").codigoHex("#2E7D32").stockMetros(200.0).stockMinimo(20.0).proveedor("Textiles Colombia").precioMetro(0.65).build());
        var cAmarillo = colorHiloRepository.save(ColorHilo.builder().id("c-amarillo").nombre("Amarillo Sol").codigoHex("#FDD835").stockMetros(350.0).stockMinimo(30.0).proveedor("Hilos del Sur").precioMetro(0.5).build());
        var cMorado   = colorHiloRepository.save(ColorHilo.builder().id("c-morado").nombre("Morado Real").codigoHex("#6A1B9A").stockMetros(180.0).stockMinimo(20.0).proveedor("Textiles Colombia").precioMetro(0.7).build());
        var cNaranja  = colorHiloRepository.save(ColorHilo.builder().id("c-naranja").nombre("Naranja Coral").codigoHex("#FF7043").stockMetros(250.0).stockMinimo(25.0).proveedor("Hilos del Sur").precioMetro(0.55).build());
        var cGris     = colorHiloRepository.save(ColorHilo.builder().id("c-gris").nombre("Gris Perla").codigoHex("#BDBDBD").stockMetros(600.0).stockMinimo(50.0).proveedor("ImportHilos").precioMetro(0.45).build());
        var cCafe     = colorHiloRepository.save(ColorHilo.builder().id("c-cafe").nombre("Café Oscuro").codigoHex("#4E342E").stockMetros(100.0).stockMinimo(15.0).proveedor("ImportHilos").precioMetro(0.75).build());
        var cRojo     = colorHiloRepository.save(ColorHilo.builder().id("c-rojo").nombre("Rojo Cereza").codigoHex("#D32F2F").stockMetros(220.0).stockMinimo(25.0).proveedor("Textiles Colombia").precioMetro(0.6).build());
        var cTurquesa = colorHiloRepository.save(ColorHilo.builder().id("c-turquesa").nombre("Turquesa").codigoHex("#00BCD4").stockMetros(150.0).stockMinimo(15.0).proveedor("Hilos del Sur").precioMetro(0.65).build());
        var cCoral    = colorHiloRepository.save(ColorHilo.builder().id("c-coral").nombre("Coral").codigoHex("#FF8A80").stockMetros(280.0).stockMinimo(30.0).proveedor("Textiles Colombia").precioMetro(0.55).build());

        // ── Productos (13) ─────────────────────────────────────────────
        var pConejo     = productoRepository.save(Producto.builder().id("prod-conejo").nombre("Amigurumi Conejo").descripcion("Conejo tejido a mano en algodón hipoalergénico").precioBase(35000.0).categoria("Amigurumis").stock(10).coloresDisponibles(List.of(cBlanco.getId(),cRosa.getId())).tiempoElaboracionDias(3).build());
        var pBufanda    = productoRepository.save(Producto.builder().id("prod-bufanda").nombre("Bufanda Tejida").descripcion("Bufanda larga de lana merino, diseño clásico").precioBase(25000.0).categoria("Accesorios").stock(15).coloresDisponibles(List.of(cAzul.getId(),cVerde.getId())).tiempoElaboracionDias(2).build());
        var pCobija     = productoRepository.save(Producto.builder().id("prod-cobija").nombre("Cobija Patchwork").descripcion("Cobija multicolor elaborada en técnica patchwork").precioBase(80000.0).categoria("Hogar").stock(5).coloresDisponibles(List.of(cBlanco.getId(),cAzul.getId(),cRosa.getId())).tiempoElaboracionDias(7).build());
        var pGorro      = productoRepository.save(Producto.builder().id("prod-gorro").nombre("Gorro Navideño").descripcion("Gorro navideño tejido con hilo brillante").precioBase(15000.0).categoria("Accesorios").stock(20).coloresDisponibles(List.of(cRojo.getId(),cVerde.getId())).tiempoElaboracionDias(1).build());
        var pOso        = productoRepository.save(Producto.builder().id("prod-oso").nombre("Amigurumi Oso").descripcion("Osito de peluche tejido a mano, 25 cm").precioBase(40000.0).categoria("Amigurumis").stock(8).coloresDisponibles(List.of(cCafe.getId(),cGris.getId())).tiempoElaboracionDias(4).build());
        var pGato       = productoRepository.save(Producto.builder().id("prod-gato").nombre("Amigurumi Gato").descripcion("Gatito tejido con bigotes de hilo fino").precioBase(38000.0).categoria("Amigurumis").stock(6).coloresDisponibles(List.of(cNaranja.getId(),cGris.getId(),cBlanco.getId())).tiempoElaboracionDias(3).build());
        var pBolso      = productoRepository.save(Producto.builder().id("prod-bolso").nombre("Bolso Tejido").descripcion("Bolso playero tejido en fibra natural").precioBase(45000.0).categoria("Accesorios").stock(12).coloresDisponibles(List.of(cAmarillo.getId(),cTurquesa.getId())).tiempoElaboracionDias(3).build());
        var pPosavasos  = productoRepository.save(Producto.builder().id("prod-posavasos").nombre("Set Posavasos").descripcion("Juego de 6 posavasos tejidos").precioBase(12000.0).categoria("Hogar").stock(25).coloresDisponibles(List.of(cGris.getId(),cVerde.getId(),cCoral.getId())).tiempoElaboracionDias(1).build());
        var pCojin      = productoRepository.save(Producto.builder().id("prod-cojin").nombre("Cojín Decorativo").descripcion("Cojín circular tejido con diseño étnico").precioBase(32000.0).categoria("Hogar").stock(7).coloresDisponibles(List.of(cMorado.getId(),cNaranja.getId(),cAmarillo.getId())).tiempoElaboracionDias(4).build());
        var pSueter     = productoRepository.save(Producto.builder().id("prod-sueter").nombre("Suéter Tejido").descripcion("Suéter de lana con trenzas clásicas").precioBase(65000.0).categoria("Prendas").stock(4).coloresDisponibles(List.of(cAzul.getId(),cGris.getId(),cRojo.getId())).tiempoElaboracionDias(6).build());
        var pUnicornio  = productoRepository.save(Producto.builder().id("prod-unicornio").nombre("Amigurumi Unicornio").descripcion("Unicornio mágico con crin de colores").precioBase(42000.0).categoria("Amigurumis").stock(3).coloresDisponibles(List.of(cBlanco.getId(),cRosa.getId(),cMorado.getId())).tiempoElaboracionDias(4).build());
        var pNeceser    = productoRepository.save(Producto.builder().id("prod-neceser").nombre("Neceser Tejido").descripcion("Neceser con cierre, ideal para maquillaje").precioBase(18000.0).categoria("Accesorios").stock(18).coloresDisponibles(List.of(cRosa.getId(),cCoral.getId(),cTurquesa.getId())).tiempoElaboracionDias(1).build());
        var pCuello     = productoRepository.save(Producto.builder().id("prod-cuello").nombre("Cuello Tejido").descripcion("Cuello infinito de lana suave").precioBase(22000.0).categoria("Prendas").stock(14).coloresDisponibles(List.of(cGris.getId(),cCafe.getId(),cVerde.getId())).tiempoElaboracionDias(2).build());

        // ── Usuarios (9) ───────────────────────────────────────────────
        var uLogistica  = usuarioRepository.save(Usuario.builder().id("usr-logistica").nombre("María Logística").correo("logistica@sarichi.com").passwordHash(pwd).telefono("3001112222").rol(UserRole.LOGISTICA).build());
        var uArtesana   = usuarioRepository.save(Usuario.builder().id("usr-artesana").nombre("Sofía Artesana").correo("artesana@sarichi.com").passwordHash(pwd).telefono("3003334444").rol(UserRole.ARTESANA).build());
        var uAdmin      = usuarioRepository.save(Usuario.builder().id("usr-admin").nombre("Admin Sarichi").correo("admin@sarichi.com").passwordHash(pwd).telefono("3005556666").rol(UserRole.ADMIN).build());
        var uMercadeo   = usuarioRepository.save(Usuario.builder().id("usr-mercadeo").nombre("Camila Mercadeo").correo("mercadeo@sarichi.com").passwordHash(pwd).telefono("3011112233").rol(UserRole.MERCADEO).build());
        var uBodega     = usuarioRepository.save(Usuario.builder().id("usr-bodega").nombre("Pedro Bodega").correo("bodega@sarichi.com").passwordHash(pwd).telefono("3022223344").rol(UserRole.BODEGA).build());
        var uCliente1   = usuarioRepository.save(Usuario.builder().id("usr-cliente1").nombre("Ana Martínez").correo("ana@email.com").passwordHash(pwd).telefono("3007778888").rol(UserRole.CLIENTE).direcciones(List.of("Calle 45 #12-34, Bogotá")).build());
        var uCliente2   = usuarioRepository.save(Usuario.builder().id("usr-cliente2").nombre("Carlos López").correo("carlos@email.com").passwordHash(pwd).telefono("3009990000").rol(UserRole.CLIENTE).direcciones(List.of("Cra 7 #89-01, Medellín")).build());
        var uCliente3   = usuarioRepository.save(Usuario.builder().id("usr-cliente3").nombre("Valentina Ríos").correo("valentina@email.com").passwordHash(pwd).telefono("3101112222").rol(UserRole.CLIENTE).direcciones(List.of("Calle 10 #20-30, Cali")).build());
        var uCliente4   = usuarioRepository.save(Usuario.builder().id("usr-cliente4").nombre("Daniel Torres").correo("daniel@email.com").passwordHash(pwd).telefono("3113334444").rol(UserRole.CLIENTE).direcciones(List.of("Cra 50 #15-60, Barranquilla")).build());
        var uCliente5   = usuarioRepository.save(Usuario.builder().id("usr-cliente5").nombre("Laura Gómez").correo("laura@email.com").passwordHash(pwd).telefono("3125556666").rol(UserRole.CLIENTE).direcciones(List.of("Av 3N #5-20, Bucaramanga")).build());

        var clientes = List.of(uCliente1, uCliente2, uCliente3, uCliente4, uCliente5);
        var todosProductos = List.of(pConejo, pBufanda, pCobija, pGorro, pOso, pGato, pBolso, pPosavasos, pCojin, pSueter, pUnicornio, pNeceser, pCuello);

        // ── Pedidos (15, variados estados y fechas) ────────────────────
        for (int i = 0; i < 15; i++) {
            var cli = clientes.get(i % clientes.size());
            var prod = todosProductos.get(i % todosProductos.size());
            int offset = i * 2;
            var fecha = ahora.minusDays(Math.max(1, 30 - offset));
            var estado = switch (i) {
                case 0, 1 -> "SOLICITADO";
                case 2, 3 -> "EN_PRODUCCION";
                case 4, 5, 6 -> "LISTO";
                case 7, 8, 9 -> "DESPACHADO";
                case 10, 11, 12, 13, 14 -> "ENTREGADO";
                default -> "SOLICITADO";
            };
            var diasProd = prod.getTiempoElaboracionDias() != null ? prod.getTiempoElaboracionDias() : 3;
            pedidoRepository.save(Pedido.builder()
                    .id("pedido-" + (100 + i))
                    .usuarioId(cli.getId())
                    .fechaPedido(fecha)
                    .total(prod.getPrecioBase())
                    .estado(estado)
                    .direccionEnvio(cli.getDirecciones().get(0))
                    .fechaEntregaEstimada(estado.equals("ENTREGADO") || estado.equals("DESPACHADO") ? fecha.plusDays(diasProd + 2) : null)
                    .items(List.of(ItemPedido.builder()
                            .productoId(prod.getId()).nombreProducto(prod.getNombre())
                            .cantidad(1).colorSolicitado(prod.getColoresDisponibles().get(0))
                            .precioUnitario(prod.getPrecioBase()).subtotal(prod.getPrecioBase())
                            .build()))
                    .build());
        }

        // ── Despachos (para DESPACHADO y ENTREGADO) ────────────────────
        despachoRepository.save(Despacho.builder().id("desp-001").pedidoId("pedido-107").fechaDespacho(ahora.minusDays(5)).transportadora("Coordinadora").numeroGuia("GU-2026-001").estado("EN_TRANSITO").fechaEstimadaEntrega(ahora.plusDays(2)).observaciones("Entregar en horario laboral").build());
        despachoRepository.save(Despacho.builder().id("desp-002").pedidoId("pedido-108").fechaDespacho(ahora.minusDays(7)).transportadora("Interrapidísimo").numeroGuia("GU-2026-002").estado("EN_TRANSITO").fechaEstimadaEntrega(ahora.minusDays(1)).observaciones("Llamar antes de entregar").build());
        despachoRepository.save(Despacho.builder().id("desp-003").pedidoId("pedido-109").fechaDespacho(ahora.minusDays(9)).transportadora("Servientrega").numeroGuia("GU-2026-003").estado("EN_TRANSITO").fechaEstimadaEntrega(ahora.minusDays(3)).observaciones("Dejar con portero").build());
        despachoRepository.save(Despacho.builder().id("desp-004").pedidoId("pedido-110").fechaDespacho(ahora.minusDays(12)).transportadora("Coordinadora").numeroGuia("GU-2026-004").estado("ENTREGADO").fechaEstimadaEntrega(ahora.minusDays(8)).observaciones("Entregado a satisfacción").build());
        despachoRepository.save(Despacho.builder().id("desp-005").pedidoId("pedido-111").fechaDespacho(ahora.minusDays(15)).transportadora("Interrapidísimo").numeroGuia("GU-2026-005").estado("ENTREGADO").fechaEstimadaEntrega(ahora.minusDays(11)).observaciones("Recibido por el cliente").build());
        despachoRepository.save(Despacho.builder().id("desp-006").pedidoId("pedido-112").fechaDespacho(ahora.minusDays(18)).transportadora("Servientrega").numeroGuia("GU-2026-006").estado("ENTREGADO").fechaEstimadaEntrega(ahora.minusDays(14)).observaciones("Entregado en portería").build());
        despachoRepository.save(Despacho.builder().id("desp-007").pedidoId("pedido-113").fechaDespacho(ahora.minusDays(20)).transportadora("Coordinadora").numeroGuia("GU-2026-007").estado("ENTREGADO").fechaEstimadaEntrega(ahora.minusDays(16)).observaciones("Todo en orden").build());
        despachoRepository.save(Despacho.builder().id("desp-008").pedidoId("pedido-114").fechaDespacho(ahora.minusDays(22)).transportadora("Interrapidísimo").numeroGuia("GU-2026-008").estado("ENTREGADO").fechaEstimadaEntrega(ahora.minusDays(18)).observaciones("Cliente feliz").build());

        // ── Actividades de producción (10) ─────────────────────────────
        actividadRepository.save(ActividadProduccion.builder().id("act-001").pedidoId("pedido-100").productoId(pConejo.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(3)).estado("EN_PROGRESO").notas("Tejiendo cuerpo del conejo").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-002").pedidoId("pedido-101").productoId(pBufanda.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(2)).estado("EN_PROGRESO").notas("Bufanda en punto inglés").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-003").pedidoId("pedido-102").productoId(pCobija.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(5)).fechaFinalizacion(ahora.minusDays(1)).estado("COMPLETADO").notas("Cobija patchwork terminada").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-004").pedidoId("pedido-103").productoId(pGorro.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(4)).fechaFinalizacion(ahora.minusDays(1)).estado("COMPLETADO").notas("Gorro navideño listo").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-005").pedidoId("pedido-104").productoId(pGato.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(6)).fechaFinalizacion(ahora.minusDays(2)).estado("COMPLETADO").notas("Gatito terminado con bigotes").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-006").pedidoId("pedido-105").productoId(pBolso.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(7)).fechaFinalizacion(ahora.minusDays(3)).estado("COMPLETADO").notas("Bolso playero terminado").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-007").pedidoId("pedido-106").productoId(pPosavasos.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(3)).fechaFinalizacion(ahora.minusDays(1)).estado("COMPLETADO").notas("Set de posavasos listo").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-008").pedidoId("pedido-110").productoId(pCojin.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(14)).fechaFinalizacion(ahora.minusDays(9)).estado("COMPLETADO").notas("Cojín decorativo terminado").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-009").pedidoId("pedido-112").productoId(pSueter.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(20)).fechaFinalizacion(ahora.minusDays(13)).estado("COMPLETADO").notas("Suéter con trenzas completado").build());
        actividadRepository.save(ActividadProduccion.builder().id("act-010").pedidoId("pedido-114").productoId(pUnicornio.getId()).artesanaId(uArtesana.getId()).cantidad(1).fechaInicio(ahora.minusDays(24)).fechaFinalizacion(ahora.minusDays(19)).estado("COMPLETADO").notas("Unicornio mágico terminado").build());

        // ── Eventos de tráfico (MongoDB, batch insert) ─────────────────
        if (eventoTraficoRepository != null) {
            try {
                var rng = ThreadLocalRandom.current();
                String[] fuentes = {"INSTAGRAM", "GOOGLE", "DIRECTO", "WHATSAPP", "FACEBOOK"};
                double[] pesosFuente = {0.30, 0.25, 0.25, 0.12, 0.08};
                String[] paginas = {"/", "/productos", "/blog", "/galeria", "/personalizador", "/blog/amigurumi-conejo", "/blog/bufandas-tendencia", "/retos"};
                double[] pesosPagina = {0.20, 0.25, 0.15, 0.10, 0.10, 0.07, 0.08, 0.05};
                String[] tipos = {"VISITA", "CLICK", "CONVERSION"};
                double[] pesoTipo = {0.70, 0.20, 0.10};

                var eventos = new java.util.ArrayList<EventoTrafico>();
                for (int dia = 0; dia < 30; dia++) {
                    int eventosHoy = dia < 2 ? rng.nextInt(10, 16) : rng.nextInt(2, 6);
                    for (int e = 0; e < eventosHoy; e++) {
                        var fecha = ahora.minusDays(dia).withHour(rng.nextInt(8, 22)).withMinute(rng.nextInt(0, 59)).withSecond(rng.nextInt(0, 59)).withNano(0);

                        double rnd = rng.nextDouble();
                        String fuente = fuentes[0]; double acum = pesosFuente[0];
                        for (int i = 1; i < fuentes.length; i++) { if (rnd <= acum) { fuente = fuentes[i-1]; break; } acum += pesosFuente[i]; }

                        String url = paginas[0]; acum = pesosPagina[0];
                        for (int i = 1; i < paginas.length; i++) { if (rnd <= acum) { url = paginas[i-1]; break; } acum += pesosPagina[i]; }

                        String tipo = tipos[0]; acum = pesoTipo[0];
                        for (int i = 1; i < tipos.length; i++) { if (rnd <= acum) { tipo = tipos[i-1]; break; } acum += pesoTipo[i]; }

                        var ev = EventoTrafico.builder()
                                .fecha(fecha).tipo(tipo).fuente(fuente).url(url)
                                .sessionId("seed-session-" + (dia * 10 + e))
                                .userAgent("Mozilla/5.0")
                                .ipAddress("192.168.1." + rng.nextInt(1, 255))
                                .build();

                        if ("CONVERSION".equals(tipo)) {
                            ev.setPedidoId("pedido-" + (100 + rng.nextInt(0, 15)));
                            ev.setValorConversion(15000.0 + rng.nextDouble() * 70000);
                        }
                        eventos.add(ev);
                    }
                }
                eventoTraficoRepository.saveAll(eventos);
                log.info("  Traffic events: {}", eventos.size());
            } catch (Exception e) {
                log.warn("Could not seed traffic events: {}", e.getMessage());
            }
        } else {
            log.info("  Traffic events: skipped (MongoDB not available)");
        }

        log.info("=== Seed data created ===");
        log.info("  Users: 10 (all pwd=123456)");
        log.info("  Products: {}", todosProductos.size());
        log.info("  Thread colors: 12");
        log.info("  Orders: 15");
        log.info("  Dispatches: 8");
        log.info("  Activities: 10");
        log.info("Roles: LOGISTICA, ARTESANA, ADMIN, MERCADEO, BODEGA, CLIENTE (5)");
    }
}
