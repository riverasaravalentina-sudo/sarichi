package com.sarichi.crocheting.config;

import com.sarichi.crocheting.security.JwtAuthenticationFilter;
import com.sarichi.crocheting.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)

            // ✅ IF_REQUIRED: permite HttpSession para /web/** (Thymeleaf)
            // Las rutas /api/** siguen siendo efectivamente stateless porque
            // el JwtAuthenticationFilter ignora /web/** por shouldNotFilter().
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .authorizeHttpRequests(authz -> authz

                // ── Capa web Thymeleaf — SIEMPRE pública ──────────────────────────
                .requestMatchers("/api/web/**").permitAll()

                // ── Recursos estáticos y HTML públicos ────────────────────────────
                .requestMatchers(
                    "/", "/*.html", "/*.css", "/*.js",
                    "/sarichi.css", "/sarichi.js", "/sarichi-ext.js",
                    "/css/**", "/js/**", "/images/**", "/fonts/**",
                    "/favicon.ico"
                ).permitAll()

                // ── API Auth pública ───────────────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ── API catálogo público ───────────────────────────────────────────
                .requestMatchers("GET", "/api/productos").permitAll()
                .requestMatchers("GET", "/api/productos/**").permitAll()
                .requestMatchers("GET", "/api/colores-hilo").permitAll()
                .requestMatchers("GET", "/api/colores-hilo/**").permitAll()
                .requestMatchers("GET", "/api/resenas/**").permitAll()
                .requestMatchers("GET", "/api/blog").permitAll()
                .requestMatchers("GET", "/api/blog/**").permitAll()
                .requestMatchers("GET", "/api/galeria").permitAll()
                .requestMatchers("GET", "/api/galeria/**").permitAll()
                .requestMatchers("GET", "/api/despachos/seguimiento/**").permitAll()

                // ── Health / Actuator ──────────────────────────────────────────────
                .requestMatchers("/api/health/**", "/api/actuator/**").permitAll()

                // ── WebSocket endpoint ─────────────────────────────────────────────
                .requestMatchers("/ws-chat/**").permitAll()

                // ── API personalizador ───────────────────────────────────────────────
                .requestMatchers("/api/personalizador/**").permitAll()

                // ── API wishlist ─────────────────────────────────────────────────────
                .requestMatchers("/api/wishlist/**").permitAll()

                // ── API mensajes (chat history) ────────────────────────────────────
                .requestMatchers("GET", "/api/mensajes/pedido/**").permitAll()
                .requestMatchers("POST", "/api/mensajes/pedido/**").permitAll()

                // ── Webhooks externos ──────────────────────────────────────────────
                .requestMatchers("POST", "/api/pagos/webhook").permitAll()

                // ── OAuth2 / Google Login ───────────────────────────────────────────
                .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()

                // ── Swagger / OpenAPI ──────────────────────────────────────────────
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // ── Todo lo demás requiere JWT ─────────────────────────────────────
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            // OAuth2 Google Login — pendiente de credenciales (Fase 2)
            // .oauth2Login(oauth2 -> oauth2
            //     .successHandler(oAuth2LoginSuccessHandler)
            //     .loginPage("/web/login")
            //     .permitAll()
            // )
            .addFilterBefore(jwtAuthenticationFilter(),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Evitar que Spring Boot registre el filtro JWT como filtro de servlet global,
    // ya que solo debe actuar cuando Spring Security lo invoca explícitamente.
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg =
                new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}
