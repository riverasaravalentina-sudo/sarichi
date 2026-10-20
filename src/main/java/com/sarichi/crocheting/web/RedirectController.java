package com.sarichi.crocheting.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Compatibilidad hacia atras para las rutas antiguas de la aplicacion web.
 *
 * Hasta el Sprint QA las paginas se servian bajo {@code /api/web/**}. Con la
 * integracion al ecosistema SARICHI se movieron a {@code /crocheting/**}.
 * Este controlador mantiene funcionando enlaces, marcadores y formularios ya
 * distribuidos, redirigiendo de forma permanente (301) al nuevo destino.
 *
 * No afecta a la Rifa Garfield, que usa rutas propias ({@code /api/rifa/**} y
 * {@code /rifa/**}) y no pasa por aqui.
 */
@Controller
public class RedirectController {

    @RequestMapping("/api/web")
    public ResponseEntity<Void> redirectWebRaiz(HttpServletRequest request) {
        return redirect("/crocheting/");
    }

    @RequestMapping("/api/web/**")
    public ResponseEntity<Void> redirectWeb(HttpServletRequest request) {
        String ruta = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String nueva;
        if (ruta == null || ruta.equals("/api/web") || ruta.equals("/api/web/")) {
            nueva = "/crocheting/";
        } else {
            nueva = ruta.replaceFirst("^/api/web", "/crocheting");
        }
        return redirect(nueva);
    }

    private ResponseEntity<Void> redirect(String destino) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header("Location", destino)
                .build();
    }
}
