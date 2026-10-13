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
@DisplayName("DespachoController Integration Tests")
class DespachoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("createDespacho_sinAutenticacion_retorna401")
    void test_create_despacho_sin_auth() throws Exception {
        mockMvc.perform(post("/api/despachos")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getDespachosSeguimiento_publico_retorna200")
    void test_get_seguimiento_publico() throws Exception {
        mockMvc.perform(get("/api/despachos/GU-0001/seguimiento"))
                .andExpect(status().isOk());
    }
}
