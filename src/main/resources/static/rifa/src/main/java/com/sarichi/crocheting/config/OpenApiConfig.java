package com.sarichi.crocheting.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sarichiOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("🐱 Crocheting Sarichi — API REST")
                .description("Plataforma web de Gestión y Venta de Tejidos Artesanales. " +
                             "Amigurumis · Accesorios · Detalles personalizados 🎀🧸")
                .version("1.0.0 — Sprint 1")
                .contact(new Contact()
                    .name("Sarichi Crocheting")
                    .email("sarichi.crocheting@gmail.com")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("Desarrollo local")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Ingresa el access token de POST /auth/login")));
    }
}