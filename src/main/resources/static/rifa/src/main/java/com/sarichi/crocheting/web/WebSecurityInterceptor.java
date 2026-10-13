package com.sarichi.crocheting.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de seguridad para la capa WEB (Thymeleaf / sesión HTTP).
 *
 * Protege las rutas /web/dashboard/** para que cada rol
 * solo pueda acceder a SU PROPIO dashboard.
 *
 * Si un CLIENTE intenta acceder a /web/dashboard/admin → redirect a /web/dashboard/cliente
 * Si no hay sesión → redirect a /web/login
 */
@Component
public class WebSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();
        String path = request.getRequestURI();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // Solo aplica a rutas de dashboard protegidas
        if (!path.startsWith("/web/dashboard")) return true;

        // Sin sesión → login
        if (session == null || session.getAttribute("usuarioWebRol") == null) {
            response.sendRedirect(contextPath + "/web/login");
            return false;
        }

        String rol = (String) session.getAttribute("usuarioWebRol");

        // /web/dashboard sin sufijo → WebController.dashboardGeneral() lo resuelve
        if (path.equals("/web/dashboard") || path.equals("/web/dashboard/")) return true;

        // Validar que el rol coincida con la ruta solicitada
        boolean permitido = switch (rol) {
            case "ADMIN"     -> path.startsWith("/web/dashboard/admin");
            case "ARTESANA"  -> path.startsWith("/web/dashboard/artesana");
            case "LOGISTICA" -> path.startsWith("/web/dashboard/logistica");
            case "BODEGA"    -> path.startsWith("/web/dashboard/bodega");
            case "MERCADEO"  -> path.startsWith("/web/dashboard/mercadeo");
            case "CLIENTE"   -> path.startsWith("/web/dashboard/cliente");
            default          -> false;
        };

        if (!permitido) {
            // Redirigir al dashboard que le corresponde
            String dashboardCorrecto = dashboardPorRol(rol);
            response.sendRedirect(contextPath + dashboardCorrecto);
            return false;
        }

        return true;
    }

    private String dashboardPorRol(String rol) {
        return switch (rol) {
            case "ADMIN"     -> "/web/dashboard/admin";
            case "ARTESANA"  -> "/web/dashboard/artesana";
            case "LOGISTICA" -> "/web/dashboard/logistica";
            case "BODEGA"    -> "/web/dashboard/bodega";
            case "MERCADEO"  -> "/web/dashboard/mercadeo";
            case "CLIENTE"   -> "/web/dashboard/cliente";
            default          -> "/web/";
        };
    }
}
