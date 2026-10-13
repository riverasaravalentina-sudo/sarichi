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
@DisplayName("DevolucionController Integration Tests")
class DevolucionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("solicitarDevolucion_sinAutenticacion_retorna401")
    void test_solicitar_devolucion_sin_auth() throws Exception {
        mockMvc.perform(post("/api/devoluciones/solicitar")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getDevoluciones_sinAutenticacion_retorna401")
    void test_get_devoluciones_sin_auth() throws Exception {
        mockMvc.perform(get("/api/devoluciones"))
                .andExpect(status().isUnauthorized());
    }
}
