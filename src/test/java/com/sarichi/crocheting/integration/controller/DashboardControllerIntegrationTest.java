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
@DisplayName("DashboardController Integration Tests")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("getDashboardKpis_sinAutenticacion_retorna401")
    void test_get_kpis_sin_auth() throws Exception {
        mockMvc.perform(get("/api/dashboard/kpis"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getVentas_sinAutenticacion_retorna401")
    void test_get_ventas_sin_auth() throws Exception {
        mockMvc.perform(get("/api/dashboard/ventas"))
                .andExpect(status().isUnauthorized());
    }
}
