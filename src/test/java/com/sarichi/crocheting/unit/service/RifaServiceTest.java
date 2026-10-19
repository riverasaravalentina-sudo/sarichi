package com.sarichi.crocheting.unit.service;

import com.sarichi.crocheting.dto.CambioEstadoRifaDTO;
import com.sarichi.crocheting.dto.PanelRifaDTO;
import com.sarichi.crocheting.dto.ReservaRifaResponseDTO;
import com.sarichi.crocheting.dto.TableroRifaDTO;
import com.sarichi.crocheting.entity.EstadoNumero;
import com.sarichi.crocheting.entity.RifaNumero;
import com.sarichi.crocheting.exception.ConflictoReservaException;
import com.sarichi.crocheting.exception.ReservaNoEncontradaException;
import com.sarichi.crocheting.repository.RifaNumeroRepository;
import com.sarichi.crocheting.service.RifaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("RifaService Unit Tests")
class RifaServiceTest {

    private static final String RIFA_ID = "garfield-2026";
    private static final String FIN_FUTURO = "2099-12-31T18:00:00";
    private static final String FIN_PASADO = "2000-01-01T18:00:00";

    @Mock
    private RifaNumeroRepository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private RifaService rifaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(rifaService, "rifaId", RIFA_ID);
        ReflectionTestUtils.setField(rifaService, "totalNumeros", 100);
        ReflectionTestUtils.setField(rifaService, "reservaHoras", 36);
        ReflectionTestUtils.setField(rifaService, "maxPorReserva", 10);
        ReflectionTestUtils.setField(rifaService, "precio", 2500L);
        ReflectionTestUtils.setField(rifaService, "zonaHoraria", "America/Bogota");
        ReflectionTestUtils.setField(rifaService, "finApartadoIso", FIN_FUTURO);
    }

    private void sinExpiradas() {
        when(repository.findByRifaIdAndEstadoAndReservadoHastaBefore(
                eq(RIFA_ID), eq(EstadoNumero.RESERVADO), any(LocalDateTime.class)))
                .thenReturn(List.of());
    }

    @Test
    @DisplayName("obtenerTablero_sinDatos_devuelve100Disponibles")
    void test_tablero_vacio() {
        sinExpiradas();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of());

        TableroRifaDTO tablero = rifaService.obtenerTablero();

        assertThat(tablero.getTotal()).isEqualTo(100);
        assertThat(tablero.getDisponibles()).isEqualTo(100);
        assertThat(tablero.getReservados()).isZero();
        assertThat(tablero.getVendidos()).isZero();
        assertThat(tablero.getNumeros()).hasSize(100);
        assertThat(tablero.getNumeros().get(0).getEstado()).isEqualTo("DISPONIBLE");
    }

    @Test
    @DisplayName("obtenerTablero_conUnVendido_cuentaTotalesCorrectos")
    void test_tablero_con_vendido() {
        sinExpiradas();
        RifaNumero vendido = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(7).estado(EstadoNumero.VENDIDO).build();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of(vendido));

        TableroRifaDTO tablero = rifaService.obtenerTablero();

        assertThat(tablero.getVendidos()).isEqualTo(1);
        assertThat(tablero.getDisponibles()).isEqualTo(99);
        assertThat(tablero.getNumeros().get(7).getEstado()).isEqualTo("VENDIDO");
    }

    @Test
    @DisplayName("reservar_numeroLibre_devuelveCodigoYNumeros")
    void test_reservar_exitoso() {
        sinExpiradas();
        when(repository.findByRifaIdAndCodigoReserva(eq(RIFA_ID), anyString()))
                .thenReturn(List.of());

        ReservaRifaResponseDTO res = rifaService.reservar(List.of(7, 8), null, null, null);

        assertThat(res.getCodigoReserva()).isNotBlank();
        assertThat(res.getNumeros()).containsExactly(7, 8);
        assertThat(res.getTotal()).isEqualTo(2);
        LocalDateTime ahoraBogota = LocalDateTime.now(ZoneId.of("America/Bogota"));
        assertThat(res.getExpiraEn()).isAfter(ahoraBogota.plusHours(35));
        assertThat(res.getExpiraEn()).isBefore(ahoraBogota.plusHours(37));
        verify(mongoTemplate, times(2)).upsert(any(Query.class), any(Update.class), eq(RifaNumero.class));
    }

    @Test
    @DisplayName("reservar_numeroOcupado_lanzaConflictoYSonLosNumeros")
    void test_reservar_conflicto() {
        sinExpiradas();
        when(repository.findByRifaIdAndCodigoReserva(eq(RIFA_ID), anyString()))
                .thenReturn(List.of());
        doThrow(new DuplicateKeyException("dup"))
                .when(mongoTemplate).upsert(any(Query.class), any(Update.class), eq(RifaNumero.class));

        assertThatThrownBy(() -> rifaService.reservar(List.of(7, 8), null, null, null))
                .isInstanceOf(ConflictoReservaException.class)
                .satisfies(e -> assertThat(((ConflictoReservaException) e).getNumerosConflicto())
                        .containsExactly(7, 8));
    }

    @Test
    @DisplayName("reservar_demasiadosNumeros_lanzaIllegalArgument")
    void test_reservar_demasiados() {
        assertThatThrownBy(() -> rifaService.reservar(
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("reservar_numeroFueraDeRango_lanzaIllegalArgument")
    void test_reservar_fuera_de_rango() {
        assertThatThrownBy(() -> rifaService.reservar(List.of(100), null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("cambiarEstado_vendido_guardaEstadoYComprador")
    void test_cambiar_estado_vendido() {
        when(repository.findByRifaIdAndNumero(RIFA_ID, 7)).thenReturn(java.util.Optional.empty());
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));
        sinExpiradas();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of());

        CambioEstadoRifaDTO dto = CambioEstadoRifaDTO.builder()
                .numeros(List.of(7))
                .estado("VENDIDO")
                .compradorNombre("Ana")
                .compradorTelefono("3001234567")
                .build();

        PanelRifaDTO panel = rifaService.cambiarEstado(dto, "panel");

        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        RifaNumero guardado = captor.getValue();
        assertThat(guardado.getEstado()).isEqualTo(EstadoNumero.VENDIDO);
        assertThat(guardado.getCompradorNombre()).isEqualTo("Ana");
        assertThat(guardado.getCompradorTelefono()).isEqualTo("3001234567");
        assertThat(guardado.getConfirmadoPor()).isEqualTo("panel");
        assertThat(panel).isNotNull();
    }

    @Test
    @DisplayName("cambiarEstado_disponible_limpiaDatosDelComprador")
    void test_cambiar_estado_disponible() {
        RifaNumero existente = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(7).estado(EstadoNumero.VENDIDO)
                .compradorNombre("Ana").compradorTelefono("300")
                .codigoReserva("ABC123").build();
        when(repository.findByRifaIdAndNumero(RIFA_ID, 7))
                .thenReturn(java.util.Optional.of(existente));
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));
        sinExpiradas();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of());

        CambioEstadoRifaDTO dto = CambioEstadoRifaDTO.builder()
                .numeros(List.of(7)).estado("DISPONIBLE").build();

        rifaService.cambiarEstado(dto, "panel");

        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        RifaNumero guardado = captor.getValue();
        assertThat(guardado.getEstado()).isEqualTo(EstadoNumero.DISPONIBLE);
        assertThat(guardado.getCompradorNombre()).isNull();
        assertThat(guardado.getCodigoReserva()).isNull();
    }

    @Test
    @DisplayName("limpiarReservasExpiradas_liberaYCuenta")
    void test_limpiar_expiradas() {
        RifaNumero expirada = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(3).estado(EstadoNumero.RESERVADO)
                .codigoReserva("XYZ").build();
        when(repository.findByRifaIdAndEstadoAndReservadoHastaBefore(
                eq(RIFA_ID), eq(EstadoNumero.RESERVADO), any(LocalDateTime.class)))
                .thenReturn(List.of(expirada));
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));

        int liberadas = rifaService.limpiarReservasExpiradas();

        assertThat(liberadas).isEqualTo(1);
        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoNumero.DISPONIBLE);
    }

    @Test
    @DisplayName("liberarPorCodigo_noLiberaNumerosVendidos")
    void test_liberar_no_toca_vendidos() {
        RifaNumero vendido = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(9).estado(EstadoNumero.VENDIDO)
                .codigoReserva("KEEP").build();
        when(repository.findByRifaIdAndCodigoReserva(RIFA_ID, "KEEP"))
                .thenReturn(List.of(vendido));

        int liberados = rifaService.liberarPorCodigo("KEEP");

        assertThat(liberados).isZero();
        verify(repository, times(0)).save(any(RifaNumero.class));
    }

    @Test
    @DisplayName("obtenerPanel_calculaRecaudado")
    void test_panel_recaudado() {
        sinExpiradas();
        RifaNumero vendido = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(5).estado(EstadoNumero.VENDIDO).build();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of(vendido));

        PanelRifaDTO panel = rifaService.obtenerPanel();

        assertThat(panel.getVendidos()).isEqualTo(1);
        assertThat(panel.getRecaudado()).isEqualTo(2500L);
        assertThat(panel.getDisponibles()).isEqualTo(99);
    }

    @Test
    @DisplayName("reservar_conDatos_seMantieneHastaLaFechaDeCorte")
    void test_reservar_con_datos() {
        sinExpiradas();
        when(repository.findByRifaIdAndCodigoReserva(eq(RIFA_ID), anyString()))
                .thenReturn(List.of());

        ReservaRifaResponseDTO res = rifaService.reservar(
                List.of(7, 8), "Ana", "3001234567", "pago nequi");

        assertThat(res.getExpiraEn()).isEqualTo(LocalDateTime.parse(FIN_FUTURO));
        verify(mongoTemplate, times(2)).upsert(any(Query.class), any(Update.class), eq(RifaNumero.class));
    }

    @Test
    @DisplayName("reservar_conCorteYaPasado_usa36Horas")
    void test_reservar_corte_pasado_fallback() {
        ReflectionTestUtils.setField(rifaService, "finApartadoIso", FIN_PASADO);
        sinExpiradas();
        when(repository.findByRifaIdAndCodigoReserva(eq(RIFA_ID), anyString()))
                .thenReturn(List.of());

        ReservaRifaResponseDTO res = rifaService.reservar(List.of(7), "Ana", "300", null);

        LocalDateTime ahoraBogota = LocalDateTime.now(ZoneId.of("America/Bogota"));
        assertThat(res.getExpiraEn()).isAfter(ahoraBogota.plusHours(35));
        assertThat(res.getExpiraEn()).isBefore(ahoraBogota.plusHours(37));
    }

    @Test
    @DisplayName("cambiarEstado_reservadoConDatos_fijaLaFechaDeCorte")
    void test_cambiar_estado_reservado_con_datos() {
        when(repository.findByRifaIdAndNumero(RIFA_ID, 7)).thenReturn(java.util.Optional.empty());
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));
        sinExpiradas();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of());

        CambioEstadoRifaDTO dto = CambioEstadoRifaDTO.builder()
                .numeros(List.of(7)).estado("RESERVADO")
                .compradorNombre("Ana").compradorTelefono("3001234567").build();

        rifaService.cambiarEstado(dto, "panel");

        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getReservadoHasta())
                .isEqualTo(LocalDateTime.parse(FIN_FUTURO));
    }

    @Test
    @DisplayName("cambiarEstado_reservadoSinDatos_usa36Horas")
    void test_cambiar_estado_reservado_sin_datos() {
        when(repository.findByRifaIdAndNumero(RIFA_ID, 7)).thenReturn(java.util.Optional.empty());
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));
        sinExpiradas();
        when(repository.findByRifaId(RIFA_ID)).thenReturn(List.of());

        CambioEstadoRifaDTO dto = CambioEstadoRifaDTO.builder()
                .numeros(List.of(7)).estado("RESERVADO").build();

        rifaService.cambiarEstado(dto, "panel");

        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        LocalDateTime ahoraBogota = LocalDateTime.now(ZoneId.of("America/Bogota"));
        assertThat(captor.getValue().getReservadoHasta()).isAfter(ahoraBogota.plusHours(35));
        assertThat(captor.getValue().getReservadoHasta()).isBefore(ahoraBogota.plusHours(37));
    }

    @Test
    @DisplayName("registrarDatosReserva_actualizaDatosYRecalculaVencimiento")
    void test_registrar_datos_reserva() {
        RifaNumero reservado = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(7).estado(EstadoNumero.RESERVADO)
                .codigoReserva("ABC123").build();
        when(repository.findByRifaIdAndCodigoReserva(RIFA_ID, "ABC123"))
                .thenReturn(List.of(reservado));
        when(repository.save(any(RifaNumero.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaRifaResponseDTO res = rifaService.registrarDatosReserva(
                "ABC123", "Ana", "3001234567", "pago");

        assertThat(res.getNumeros()).containsExactly(7);
        assertThat(res.getExpiraEn()).isEqualTo(LocalDateTime.parse(FIN_FUTURO));

        ArgumentCaptor<RifaNumero> captor = ArgumentCaptor.forClass(RifaNumero.class);
        verify(repository).save(captor.capture());
        RifaNumero guardado = captor.getValue();
        assertThat(guardado.getCompradorNombre()).isEqualTo("Ana");
        assertThat(guardado.getCompradorTelefono()).isEqualTo("3001234567");
        assertThat(guardado.getReservadoHasta()).isEqualTo(LocalDateTime.parse(FIN_FUTURO));
    }

    @Test
    @DisplayName("registrarDatosReserva_codigoInexistente_lanzaReservaNoEncontrada")
    void test_registrar_datos_codigo_inexistente() {
        when(repository.findByRifaIdAndCodigoReserva(RIFA_ID, "NOPE"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> rifaService.registrarDatosReserva("NOPE", "Ana", "300", null))
                .isInstanceOf(ReservaNoEncontradaException.class);
    }

    @Test
    @DisplayName("registrarDatosReserva_soloVendidos_lanzaReservaNoEncontrada")
    void test_registrar_datos_solo_vendidos() {
        RifaNumero vendido = RifaNumero.builder()
                .rifaId(RIFA_ID).numero(9).estado(EstadoNumero.VENDIDO)
                .codigoReserva("VEND").build();
        when(repository.findByRifaIdAndCodigoReserva(RIFA_ID, "VEND"))
                .thenReturn(List.of(vendido));

        assertThatThrownBy(() -> rifaService.registrarDatosReserva("VEND", "Ana", "300", null))
                .isInstanceOf(ReservaNoEncontradaException.class);
    }
}
