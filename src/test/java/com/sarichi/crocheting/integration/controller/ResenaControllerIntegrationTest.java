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
@DisplayName("ResenaController Integration Tests")
class ResenaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("getResenasPorProducto_publico_retorna200")
    void test_get_resenas_publico() throws Exception {
        mockMvc.perform(get("/api/resenas/producto/prod-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("postResena_sinAutenticacion_retorna401")
    void test_post_resena_sin_auth() throws Exception {
        mockMvc.perform(post("/api/resenas")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
