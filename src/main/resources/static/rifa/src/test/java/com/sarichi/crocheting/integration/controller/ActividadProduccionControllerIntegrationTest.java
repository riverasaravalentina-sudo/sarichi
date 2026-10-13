package com.sarichi.crocheting.integration.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ActividadProduccionController Integration Tests")
class ActividadProduccionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("getMisActividades_sinAutenticacion_retorna401")
    void test_get_mis_actividades_sin_auth() throws Exception {
        mockMvc.perform(get("/api/actividades/mis-actividades"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("iniciarActividad_sinAutenticacion_retorna401")
    void test_iniciar_actividad_sin_auth() throws Exception {
        mockMvc.perform(put("/api/actividades/act-1/iniciar"))
                .andExpect(status().isUnauthorized());
    }
}
