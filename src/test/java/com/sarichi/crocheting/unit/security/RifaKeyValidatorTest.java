package com.sarichi.crocheting.unit.security;

import com.sarichi.crocheting.exception.ClaveInvalidaException;
import com.sarichi.crocheting.exception.PanelNoConfiguradoException;
import com.sarichi.crocheting.security.RifaKeyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RifaKeyValidator Unit Tests")
class RifaKeyValidatorTest {

    private static final String CLAVE = "hFfWcPEbLzj0H20QZxzS89iateM90hOa";

    private RifaKeyValidator validador;

    @BeforeEach
    void setUp() {
        validador = new RifaKeyValidator();
        ReflectionTestUtils.setField(validador, "claveConfigurada", CLAVE);
    }

    @Test
    @DisplayName("claveCorrecta_noLanzaExcepcion")
    void test_clave_correcta() {
        assertThatCode(() -> validador.validar(CLAVE)).doesNotThrowAnyException();
        assertThat(validador.estaConfigurado()).isTrue();
    }

    @Test
    @DisplayName("claveIncorrecta_lanzaClaveInvalida")
    void test_clave_incorrecta() {
        assertThatThrownBy(() -> validador.validar("clave-mala"))
                .isInstanceOf(ClaveInvalidaException.class);
    }

    @Test
    @DisplayName("claveVacia_lanzaClaveInvalida")
    void test_clave_vacia() {
        assertThatThrownBy(() -> validador.validar(""))
                .isInstanceOf(ClaveInvalidaException.class);
        assertThatThrownBy(() -> validador.validar(null))
                .isInstanceOf(ClaveInvalidaException.class);
    }

    @Test
    @DisplayName("sinConfigurar_lanzaPanelNoConfigurado")
    void test_panel_sin_configurar() {
        ReflectionTestUtils.setField(validador, "claveConfigurada", "");

        assertThatThrownBy(() -> validador.validar("cualquiera"))
                .isInstanceOf(PanelNoConfiguradoException.class);
        assertThat(validador.estaConfigurado()).isFalse();
    }
}
