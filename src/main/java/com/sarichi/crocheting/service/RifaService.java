package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.CambioEstadoRifaDTO;
import com.sarichi.crocheting.dto.NumeroPanelDTO;
import com.sarichi.crocheting.dto.NumeroRifaDTO;
import com.sarichi.crocheting.dto.PanelRifaDTO;
import com.sarichi.crocheting.dto.ReservaActivaDTO;
import com.sarichi.crocheting.dto.ReservaRifaResponseDTO;
import com.sarichi.crocheting.dto.TableroRifaDTO;
import com.sarichi.crocheting.entity.EstadoNumero;
import com.sarichi.crocheting.entity.RifaNumero;
import com.sarichi.crocheting.exception.ConflictoReservaException;
import com.sarichi.crocheting.exception.ReservaNoEncontradaException;
import com.sarichi.crocheting.repository.RifaNumeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lógica de la rifa: estados de los números, reservas temporales,
 * confirmación de pagos y tablero público.
 */
@Service
@RequiredArgsConstructor
public class RifaService {

    private static final String ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RifaNumeroRepository repository;
    private final MongoTemplate mongoTemplate;

    @Value("${sarichi.rifa.id:garfield-2026}")
    private String rifaId;

    @Value("${sarichi.rifa.total-numeros:100}")
    private int totalNumeros;

    @Value("${sarichi.rifa.reserva-horas:24}")
    private int reservaHoras;

    @Value("${sarichi.rifa.max-por-reserva:10}")
    private int maxPorReserva;

    @Value("${sarichi.rifa.precio:2500}")
    private long precio;

    /** Zona horaria de la rifa (para el corte y las marcas de tiempo). */
    @Value("${sarichi.rifa.zona-horaria:America/Bogota}")
    private String zonaHoraria;

    /** Fecha/hora límite de los apartados con datos (ISO local, en la zona de la rifa). */
    @Value("${sarichi.rifa.fin-apartado:2026-10-29T18:00:00}")
    private String finApartadoIso;

    // ─── Tablero público ───────────────────────────────────────────────

    public TableroRifaDTO obtenerTablero() {
        limpiarReservasExpiradas();
        Map<Integer, EstadoNumero> estados = cargarEstados();

        List<NumeroRifaDTO> numeros = new ArrayList<>(totalNumeros);
        long disponibles = 0;
        long reservados = 0;
        long vendidos = 0;
        for (int n = 0; n < totalNumeros; n++) {
            EstadoNumero estado = estados.getOrDefault(n, EstadoNumero.DISPONIBLE);
            switch (estado) {
                case VENDIDO -> vendidos++;
                case RESERVADO -> reservados++;
                default -> disponibles++;
            }
            numeros.add(NumeroRifaDTO.builder()
                    .numero(n)
                    .estado(estado.name())
                    .build());
        }

        return TableroRifaDTO.builder()
                .rifaId(rifaId)
                .actualizadoEn(ahora())
                .total(totalNumeros)
                .disponibles(disponibles)
                .reservados(reservados)
                .vendidos(vendidos)
                .numeros(numeros)
                .build();
    }

    // ─── Reserva pública ───────────────────────────────────────────────

    public ReservaRifaResponseDTO reservar(List<Integer> numeros, String compradorNombre,
                                           String compradorTelefono, String notas) {
        List<Integer> solicitados = validarNumeros(numeros);
        limpiarReservasExpiradas();

        String codigo = generarCodigoUnico();
        LocalDateTime expira = vencimiento(compradorNombre, compradorTelefono);

        List<Integer> reservados = new ArrayList<>();
        List<Integer> conflicto = new ArrayList<>();

        for (Integer numero : solicitados) {
            if (intentarReservar(numero, codigo, expira, compradorNombre, compradorTelefono, notas)) {
                reservados.add(numero);
            } else {
                conflicto.add(numero);
            }
        }

        if (!conflicto.isEmpty()) {
            // Todo o nada: liberamos lo que alcanzamos a apartar.
            liberarPorCodigo(codigo);
            throw new ConflictoReservaException(
                    "Algunos números ya no están disponibles", conflicto);
        }

        return ReservaRifaResponseDTO.builder()
                .codigoReserva(codigo)
                .numeros(reservados)
                .total(reservados.size())
                .expiraEn(expira)
                .build();
    }

    public int liberarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return 0;
        List<RifaNumero> reservas = repository.findByRifaIdAndCodigoReserva(rifaId, codigo);
        int liberados = 0;
        for (RifaNumero r : reservas) {
            if (r.getEstado() == EstadoNumero.VENDIDO) continue;
            liberar(r);
            liberados++;
        }
        return liberados;
    }

    public int limpiarReservasExpiradas() {
        List<RifaNumero> expiradas = repository
                .findByRifaIdAndEstadoAndReservadoHastaBefore(
                        rifaId, EstadoNumero.RESERVADO, ahora());
        expiradas.forEach(this::liberar);
        return expiradas.size();
    }

    // ─── Panel privado ─────────────────────────────────────────────────

    public PanelRifaDTO obtenerPanel() {
        limpiarReservasExpiradas();
        List<RifaNumero> docs = repository.findByRifaId(rifaId);
        Map<Integer, RifaNumero> porNumero = docs.stream()
                .collect(Collectors.toMap(RifaNumero::getNumero, d -> d, (a, b) -> b));

        List<NumeroPanelDTO> numeros = new ArrayList<>(totalNumeros);
        long disponibles = 0;
        long reservados = 0;
        long vendidos = 0;
        Map<String, List<RifaNumero>> reservasPorCodigo = new LinkedHashMap<>();

        for (int n = 0; n < totalNumeros; n++) {
            RifaNumero d = porNumero.get(n);
            EstadoNumero estado = d == null ? EstadoNumero.DISPONIBLE : d.getEstado();
            switch (estado) {
                case VENDIDO -> vendidos++;
                case RESERVADO -> reservados++;
                default -> disponibles++;
            }
            if (estado == EstadoNumero.RESERVADO && d.getCodigoReserva() != null) {
                reservasPorCodigo
                        .computeIfAbsent(d.getCodigoReserva(), k -> new ArrayList<>())
                        .add(d);
            }
            numeros.add(NumeroPanelDTO.builder()
                    .numero(n)
                    .estado(estado.name())
                    .compradorNombre(d == null ? null : d.getCompradorNombre())
                    .compradorTelefono(d == null ? null : d.getCompradorTelefono())
                    .codigoReserva(d == null ? null : d.getCodigoReserva())
                    .reservadoHasta(d == null ? null : d.getReservadoHasta())
                    .notas(d == null ? null : d.getNotas())
                    .actualizadoEn(d == null ? null : d.getActualizadoEn())
                    .build());
        }

        List<ReservaActivaDTO> reservasActivas = reservasPorCodigo.entrySet().stream()
                .map(e -> {
                    List<RifaNumero> grupo = e.getValue();
                    RifaNumero primero = grupo.get(0);
                    List<Integer> ns = grupo.stream()
                            .map(RifaNumero::getNumero)
                            .sorted()
                            .collect(Collectors.toList());
                    return ReservaActivaDTO.builder()
                            .codigoReserva(e.getKey())
                            .numeros(ns)
                            .reservadoHasta(primero.getReservadoHasta())
                            .compradorNombre(primero.getCompradorNombre())
                            .compradorTelefono(primero.getCompradorTelefono())
                            .total(ns.size())
                            .build();
                })
                .sorted(Comparator.comparing(ReservaActivaDTO::getReservadoHasta,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        return PanelRifaDTO.builder()
                .rifaId(rifaId)
                .actualizadoEn(ahora())
                .total(totalNumeros)
                .disponibles(disponibles)
                .reservados(reservados)
                .vendidos(vendidos)
                .recaudado(vendidos * precio)
                .numeros(numeros)
                .reservasActivas(reservasActivas)
                .build();
    }

    public PanelRifaDTO cambiarEstado(CambioEstadoRifaDTO dto, String confirmadoPor) {
        List<Integer> numeros = validarNumeros(dto.getNumeros());
        EstadoNumero estado = parsearEstado(dto.getEstado());

        LocalDateTime momento = ahora();
        for (Integer numero : numeros) {
            RifaNumero doc = repository.findByRifaIdAndNumero(rifaId, numero)
                    .orElseGet(() -> RifaNumero.builder()
                            .rifaId(rifaId)
                            .numero(numero)
                            .build());

            doc.setEstado(estado);
            doc.setActualizadoEn(momento);

            if (estado == EstadoNumero.DISPONIBLE) {
                doc.setCompradorNombre(null);
                doc.setCompradorTelefono(null);
                doc.setCodigoReserva(null);
                doc.setReservadoHasta(null);
                doc.setNotas(null);
                doc.setConfirmadoPor(null);
            } else {
                doc.setCompradorNombre(dto.getCompradorNombre());
                doc.setCompradorTelefono(dto.getCompradorTelefono());
                doc.setNotas(dto.getNotas());
                doc.setConfirmadoPor(confirmadoPor);
                if (estado == EstadoNumero.RESERVADO) {
                    if (doc.getCodigoReserva() == null) {
                        doc.setCodigoReserva(generarCodigoUnico());
                    }
                    doc.setReservadoHasta(vencimiento(dto.getCompradorNombre(), dto.getCompradorTelefono()));
                } else {
                    doc.setReservadoHasta(null);
                }
            }
            repository.save(doc);
        }

        return obtenerPanel();
    }

    /**
     * Registra (o actualiza) los datos de la persona sobre una reserva ya
     * creada a partir de su código. El vencimiento se recalcula: si ahora hay
     * nombre o teléfono, se mantiene hasta la fecha de corte; si no, 36 horas.
     */
    public ReservaRifaResponseDTO registrarDatosReserva(String codigo, String compradorNombre,
                                                        String compradorTelefono, String notas) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de reserva es obligatorio");
        }

        List<RifaNumero> reservas = repository.findByRifaIdAndCodigoReserva(rifaId, codigo)
                .stream()
                .filter(r -> r.getEstado() == EstadoNumero.RESERVADO)
                .collect(Collectors.toList());
        if (reservas.isEmpty()) {
            throw new ReservaNoEncontradaException(
                    "No existe una reserva activa con el código " + codigo);
        }

        LocalDateTime expira = vencimiento(compradorNombre, compradorTelefono);
        LocalDateTime momento = ahora();

        List<Integer> numeros = new ArrayList<>(reservas.size());
        for (RifaNumero r : reservas) {
            r.setCompradorNombre(compradorNombre);
            r.setCompradorTelefono(compradorTelefono);
            r.setNotas(notas);
            r.setReservadoHasta(expira);
            r.setActualizadoEn(momento);
            repository.save(r);
            numeros.add(r.getNumero());
        }
        numeros.sort(Comparator.naturalOrder());

        return ReservaRifaResponseDTO.builder()
                .codigoReserva(codigo)
                .numeros(numeros)
                .total(numeros.size())
                .expiraEn(expira)
                .build();
    }

    public String getRifaId() {
        return rifaId;
    }

    // ─── Internos ──────────────────────────────────────────────────────

    /** Zona horaria configurada de la rifa. */
    private ZoneId zona() {
        return ZoneId.of(zonaHoraria);
    }

    /** Instante actual en la zona de la rifa (evita la hora UTC del servidor). */
    private LocalDateTime ahora() {
        return LocalDateTime.now(zona());
    }

    /** Fecha y hora límite de los apartados con datos. */
    private LocalDateTime finApartado() {
        return LocalDateTime.parse(finApartadoIso);
    }

    /** ¿El apartado queda atado a una persona? (basta nombre o teléfono). */
    private static boolean hayDatos(String compradorNombre, String compradorTelefono) {
        return (compradorNombre != null && !compradorNombre.isBlank())
                || (compradorTelefono != null && !compradorTelefono.isBlank());
    }

    /**
     * Vencimiento de un apartado: si tiene datos y la fecha de corte aún no
     * pasó, se mantiene hasta el corte; si no, dura {@code reserva-horas} horas.
     */
    private LocalDateTime vencimiento(String compradorNombre, String compradorTelefono) {
        LocalDateTime fin = finApartado();
        if (hayDatos(compradorNombre, compradorTelefono) && fin.isAfter(ahora())) {
            return fin;
        }
        return ahora().plusHours(reservaHoras);
    }

    /**
     * Intenta apartar un número de forma atómica. Solo tiene éxito si el
     * documento no existe (número nunca tocado) o está en DISPONIBLE.
     * Si ya está RESERVADO o VENDIDO, el índice único provoca un
     * DuplicateKeyException que interpretamos como conflicto.
     */
    private boolean intentarReservar(int numero, String codigo, LocalDateTime hasta,
                                     String compradorNombre, String compradorTelefono, String notas) {
        Query query = new Query(Criteria.where("rifaId").is(rifaId)
                .and("numero").is(numero)
                .orOperator(
                        Criteria.where("estado").is(EstadoNumero.DISPONIBLE),
                        Criteria.where("estado").is(null)));

        Update update = new Update()
                .set("estado", EstadoNumero.RESERVADO)
                .set("codigoReserva", codigo)
                .set("reservadoHasta", hasta)
                .set("compradorNombre", compradorNombre)
                .set("compradorTelefono", compradorTelefono)
                .set("notas", notas)
                .set("actualizadoEn", ahora())
                .setOnInsert("rifaId", rifaId)
                .setOnInsert("numero", numero);

        try {
            mongoTemplate.upsert(query, update, RifaNumero.class);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    private void liberar(RifaNumero r) {
        r.setEstado(EstadoNumero.DISPONIBLE);
        r.setCompradorNombre(null);
        r.setCompradorTelefono(null);
        r.setCodigoReserva(null);
        r.setReservadoHasta(null);
        r.setNotas(null);
        r.setConfirmadoPor(null);
        r.setActualizadoEn(ahora());
        repository.save(r);
    }

    private Map<Integer, EstadoNumero> cargarEstados() {
        return repository.findByRifaId(rifaId).stream()
                .collect(Collectors.toMap(
                        RifaNumero::getNumero,
                        RifaNumero::getEstado,
                        (a, b) -> b));
    }

    private List<Integer> validarNumeros(List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos un número");
        }
        List<Integer> limpios = numeros.stream()
                .filter(java.util.Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (limpios.size() > maxPorReserva) {
            throw new IllegalArgumentException(
                    "Solo puedes seleccionar hasta " + maxPorReserva + " números a la vez");
        }
        for (Integer n : limpios) {
            if (n < 0 || n >= totalNumeros) {
                throw new IllegalArgumentException(
                        "Número fuera de rango: " + n);
            }
        }
        return limpios;
    }

    private EstadoNumero parsearEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        try {
            return EstadoNumero.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Estado inválido: " + estado + " (usa DISPONIBLE, RESERVADO o VENDIDO)");
        }
    }

    private String generarCodigoUnico() {
        for (int intento = 0; intento < 20; intento++) {
            String codigo = generarCodigo();
            if (repository.findByRifaIdAndCodigoReserva(rifaId, codigo).isEmpty()) {
                return codigo;
            }
        }
        return generarCodigo() + generarCodigo();
    }

    private String generarCodigo() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
        }
        return sb.toString();
    }
}
