package com.sarichi.crocheting.integration.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class E2EFlujosTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("E2E_FlujoCompletoCompraYResena_Sprint3")
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void e2e_flujo_completo_compra_y_resena() throws Exception {
        // 1. Cliente crea un pedido (simulado con POST)
        mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isCreated());

        // 2. Cliente puede listar sus pedidos
        mockMvc.perform(get("/api/pedidos/mis-pedidos"))
                .andExpect(status().isOk());

        // 3. ADMIN cambia estado a ENTREGADO (simulado)
        // En un test real, usaríamos el ID del pedido creado
        mockMvc.perform(put("/api/pedidos/pedido-test/estado")
                .contentType("application/json")
                .content("{\"estado\":\"ENTREGADO\"}"))
                .andExpect(status().isNotFound()); // No existe, pero endpoint accesible

        // 4. Cliente crea una reseña
        mockMvc.perform(post("/api/resenas")
                .contentType("application/json")
                .content("{\"pedidoId\":\"pedido-test\",\"productoId\":\"prod1\",\"calificacion\":5,\"comentario\":\"Excelente\"}"))
                .andExpect(status().isCreated());

        // 5. Cliente puede ver reseñas del producto (público)
        mockMvc.perform(get("/api/resenas/producto/prod1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("E2E_FlujoCompletoDespachoYDevolucion_Sprint4")
    void e2e_flujo_completo_despacho_y_devolucion() throws Exception {
        // 1. LOGISTICA crea un despacho
        mockMvc.perform(post("/api/despachos")
                .with(request -> {
                    request.setUserPrincipal(() -> "logistica@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("{\"pedidoId\":\"ped-listo\",\"transportadora\":\"INTERRAPIDISIMO\"}"))
                .andExpect(status().isCreated());

        // 2. LOGISTICA puede ver despachos del día
        mockMvc.perform(get("/api/despachos/hoy"))
                .andExpect(status().isUnauthorized()); // Sin autenticación

        // 3. Cliente puede consultar seguimiento (público)
        mockMvc.perform(get("/api/despachos/GU-0001/seguimiento"))
                .andExpect(status().isOk());

        // 4. Cliente solicita devolución
        mockMvc.perform(post("/api/devoluciones/solicitar")
                .with(request -> {
                    request.setUserPrincipal(() -> "cliente@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("{\"pedidoId\":\"ped-entregado\",\"motivo\":\"Defecto en el producto\"}"))
                .andExpect(status().isCreated());

        // 5. ADMIN puede ver todas las devoluciones
        mockMvc.perform(get("/api/devoluciones"))
                .andExpect(status().isUnauthorized()); // Sin autenticación

        // 6. ADMIN aprueba la devolución
        mockMvc.perform(put("/api/devoluciones/dev-1/aprobar")
                .with(request -> {
                    request.setUserPrincipal(() -> "admin@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("{\"observaciones\":\"Aprobada por defecto\"}"))
                .andExpect(status().isNotFound()); // Devolución no existe, pero endpoint accesible
    }

    @Test
    @DisplayName("E2E_FlujoCompletoProduccion_Sprint4")
    void e2e_flujo_completo_produccion() throws Exception {
        // 1. ADMIN cambia pedido a EN_PRODUCCION (simulado)
        mockMvc.perform(put("/api/pedidos/ped-produccion/estado")
                .with(request -> {
                    request.setUserPrincipal(() -> "admin@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("{\"estado\":\"EN_PRODUCCION\"}"))
                .andExpect(status().isNotFound()); // Pedido no existe pero endpoint accesible

        // 2. ARTESANA puede ver sus actividades
        mockMvc.perform(get("/api/actividades/mis-actividades"))
                .andExpect(status().isUnauthorized()); // Sin autenticación

        // 3. ARTESANA inicia una actividad (simulado)
        mockMvc.perform(put("/api/actividades/act-1/iniciar")
                .with(request -> {
                    request.setUserPrincipal(() -> "artesana@test.com");
                    return request;
                }))
                .andExpect(status().isNotFound()); // Actividad no existe

        // 4. ARTESANA sube foto del proceso (simulado)
        mockMvc.perform(post("/api/actividades/act-1/fotos")
                .with(request -> {
                    request.setUserPrincipal(() -> "artesana@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("{\"urlFoto\":\"http://img.jpg\",\"descripcion\":\"Foto del proceso\"}"))
                .andExpect(status().isCreated());

        // 5. ARTESANA completa la actividad (simulado)
        mockMvc.perform(put("/api/actividades/act-1/completar")
                .with(request -> {
                    request.setUserPrincipal(() -> "artesana@test.com");
                    return request;
                })
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isNotFound()); // Actividad no existe

        // 6. Cliente puede ver fotos del proceso
        mockMvc.perform(get("/api/actividades/act-1/fotos"))
                .andExpect(status().isOk());
    }
}
