package com.sarichi.crocheting.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sarichi.crocheting.entity.UserRole;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final UsuarioRepository usuarioRepository;

    public OAuth2LoginSuccessHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String googleId = (String) attributes.get("sub");

        log.info("OAuth2 login exitoso para: {} ({})", email, name);

        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        if (usuario == null) {
            usuario = Usuario.builder()
                    .nombre(name)
                    .correo(email)
                    .rol(UserRole.CLIENTE)
                    .googleId(googleId)
                    .estado("ACTIVO")
                    .fechaRegistro(LocalDateTime.now())
                    .ultimoLogin(LocalDateTime.now())
                    .build();
            usuarioRepository.save(usuario);
            log.info("Nuevo usuario creado desde OAuth2: {}", email);
        } else {
            usuario.setGoogleId(googleId);
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioWeb", usuario.getNombre());
        session.setAttribute("usuarioWebRol", usuario.getRol().name());
        session.setAttribute("usuarioWebId", usuario.getId());

        String redirectUrl = switch (usuario.getRol().name()) {
            case "ADMIN"     -> "/api/web/dashboard/admin";
            case "ARTESANA"  -> "/api/web/dashboard/artesana";
            case "LOGISTICA" -> "/api/web/dashboard/logistica";
            case "BODEGA"    -> "/api/web/dashboard/bodega";
            case "MERCADEO"  -> "/api/web/dashboard/mercadeo";
            default          -> "/api/web/dashboard/cliente";
        };

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
