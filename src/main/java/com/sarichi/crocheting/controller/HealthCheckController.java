package com.sarichi.crocheting.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Verificación del estado del sistema")
public class HealthCheckController {

    private static final Logger log =
        LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * GET /api/health/db
     * Verifica la conexión con MongoDB Atlas
     */
    @GetMapping("/db")
    @Operation(summary = "Estado de MongoDB Atlas",
               description = "Verifica que la conexión con MongoDB Atlas esté activa.")
    public ResponseEntity<Map<String, Object>> healthDb() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "MongoDB Atlas");

        try {
            // Ejecutar un ping a MongoDB
            mongoTemplate.getDb().runCommand(
                new org.bson.Document("ping", 1));
            response.put("status", "UP");
            response.put("database",
                mongoTemplate.getDb().getName());
            response.put("message",
                "Conexión con MongoDB Atlas establecida correctamente");
            log.debug("Health check MongoDB: UP");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("message",
                "Error de conexión con MongoDB Atlas");
            log.error("Health check MongoDB: DOWN - {}", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * GET /api/health/ready
     * Readiness probe para Render/Docker
     */
    @GetMapping("/ready")
    @Operation(summary = "Readiness probe",
               description = "Verifica que la aplicación está lista para recibir tráfico.")
    public ResponseEntity<Map<String, Object>> ready() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "READY");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("sprint", "Sprint 5");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/health/info
     * Información general del sistema
     */
    @GetMapping("/info")
    @Operation(summary = "Información del sistema")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("app", "Crocheting Sarichi");
        response.put("version", "1.0.0 — Sprint 5");
        response.put("stack", "Spring Boot 3.4.5 + MongoDB Atlas");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("javaVersion",
            System.getProperty("java.version"));
        response.put("os",
            System.getProperty("os.name"));
        return ResponseEntity.ok(response);
    }
}