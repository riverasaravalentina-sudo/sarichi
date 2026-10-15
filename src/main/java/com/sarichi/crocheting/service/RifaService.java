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
                .actualizadoEn(LocalDateTime.now())
                .total(totalNumeros)
                .disponibles(disponibles)
                .reservados(reservados)
                .vendidos(vendidos)
                .numeros(numeros)
                .build();
    }

    // ─── Reserva pública ───────────────────────────────────────────────

    public ReservaRifaResponseDTO reservar(List<Integer> numeros) {
        List<Integer> solicitados = validarNumeros(numeros);
        limpiarReservasExpiradas();

        String codigo = generarCodigoUnico();
        LocalDateTime expira = LocalDateTime.now().plusHours(reservaHoras);

        List<Integer> reservados = new ArrayList<>();
        List<Integer> conflicto = new ArrayList<>();

        for (Integer numero : solicitados) {
            if (intentarReservar(numero, codigo, expira)) {
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
                        rifaId, EstadoNumero.RESERVADO, LocalDateTime.now());
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
                .actualizadoEn(LocalDateTime.now())
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

        LocalDateTime ahora = LocalDateTime.now();
        for (Integer numero : numeros) {
            RifaNumero doc = repository.findByRifaIdAndNumero(rifaId, numero)
                    .orElseGet(() -> RifaNumero.builder()
                            .rifaId(rifaId)
                            .numero(numero)
                            .build());

            doc.setEstado(estado);
            doc.setActualizadoEn(ahora);

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
                    doc.setReservadoHasta(ahora.plusHours(reservaHoras));
                } else {
                    doc.setReservadoHasta(null);
                }
            }
            repository.save(doc);
        }

        return obtenerPanel();
    }

    public String getRifaId() {
        return rifaId;
    }

    // ─── Internos ──────────────────────────────────────────────────────

    /**
     * Intenta apartar un número de forma atómica. Solo tiene éxito si el
     * documento no existe (número nunca tocado) o está en DISPONIBLE.
     * Si ya está RESERVADO o VENDIDO, el índice único provoca un
     * DuplicateKeyException que interpretamos como conflicto.
     */
    private boolean intentarReservar(int numero, String codigo, LocalDateTime hasta) {
        Query query = new Query(Criteria.where("rifaId").is(rifaId)
                .and("numero").is(numero)
                .orOperator(
                        Criteria.where("estado").is(EstadoNumero.DISPONIBLE),
                        Criteria.where("estado").is(null)));

        Update update = new Update()
                .set("estado", EstadoNumero.RESERVADO)
                .set("codigoReserva", codigo)
                .set("reservadoHasta", hasta)
                .set("actualizadoEn", LocalDateTime.now())
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
        r.setActualizadoEn(LocalDateTime.now());
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
