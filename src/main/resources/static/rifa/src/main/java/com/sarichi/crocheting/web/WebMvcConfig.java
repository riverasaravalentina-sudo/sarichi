package com.sarichi.crocheting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC para la capa web (Thymeleaf).
 * Registra el interceptor de seguridad por rol.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebSecurityInterceptor webSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webSecurityInterceptor)
                // Proteger SOLO rutas de dashboard con rol específico
                .addPathPatterns("/web/dashboard/**")
                // Excluir la ruta genérica /web/dashboard (sin sufijo),
                // que ya tiene lógica de redirección en el WebController
                .excludePathPatterns("/web/dashboard");
    }
}
