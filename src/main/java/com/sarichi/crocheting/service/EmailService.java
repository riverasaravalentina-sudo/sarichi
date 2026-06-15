package com.sarichi.crocheting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio de correos tolerante a configuración incompleta.
 *
 * En desarrollo o Render, la aplicación no debe caerse si aún no existen
 * credenciales SMTP. Cuando JavaMailSender no está disponible o el envío falla,
 * se deja registro en logs y el flujo principal continúa.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailConfigurado;

    @Value("${sarichi.mail.from:noreply@sarichi.com}")
    private String remitente;

    @Value("${sarichi.app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String getBaseUrl() { return baseUrl; }

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${spring.mail.username:}") String mailUsername) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailConfigurado = this.mailSender != null
                && mailUsername != null && !mailUsername.isBlank();
        if (!this.mailConfigurado) {
            log.info("Correo SMTP no configurado. Los correos se mostrarán en logs.");
        }
    }

    public boolean enviarCorreoRecuperacion(String correo, String nombre, String token) {
        String enlace = baseUrl + "/restablecer.html?token=" + token;
        String mensaje = "Hola " + nombre + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Haz clic en el siguiente enlace para crear una nueva contraseña:\n"
                + enlace + "\n\n"
                + "Este enlace expira en 15 minutos.\n\n"
                + "Si no solicitaste este cambio, ignora este correo.\n\n"
                + "— Crocheting Sarichi 🧵 UTS 2026";
        log.info("Enlace de recuperación para {}: {}", correo, enlace);
        return enviarSimple(correo, "Recuperación de contraseña - Sarichi", mensaje);
    }

    public void enviarConfirmacionPedido(String correo, Object pedido) {
        enviarSimple(correo, "Pedido confirmado - Sarichi",
                "Tu pedido fue recibido correctamente. Pronto iniciaremos el proceso artesanal.");
    }

    public void enviarCotizacion(String correo, Object pedido, Object precio) {
        enviarSimple(correo, "Cotización Sarichi",
                "Tu pedido personalizado ya tiene cotización: " + precio);
    }

    public void enviarConfirmacionPago(String correo, Object pago) {
        enviarSimple(correo, "Pago confirmado - Sarichi",
                "Recibimos la confirmación de tu pago. Gracias por comprar en Sarichi.");
    }

    public void enviarAlertaStockCritico(String correoAdmin, Object hilo) {
        enviarSimple(correoAdmin, "Alerta de stock crítico - Sarichi",
                "Hay un insumo con stock por debajo del mínimo. Revisa el inventario.");
    }

    public void enviarEstadoPedidoActualizado(String correo, Object pedido, String nuevoEstado) {
        enviarSimple(correo, "Estado de pedido actualizado - Sarichi",
                "Tu pedido cambió al estado: " + nuevoEstado);
    }

    public void enviarGuiaDespacho(String correo, Object despacho) {
        enviarSimple(correo, "Guía de despacho - Sarichi",
                "Tu pedido ya tiene información de despacho. Revisa el detalle en tu panel.");
    }

    private boolean enviarSimple(String para, String asunto, String contenido) {
        if (para == null || para.isBlank()) {
            log.warn("Correo omitido: destinatario vacío. Asunto: {}", asunto);
            return false;
        }

        if (!mailConfigurado) {
            log.info("Correo simulado para {} | {} | {}", para, asunto, contenido);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(para);
            message.setSubject(asunto);
            message.setText(contenido);
            mailSender.send(message);
            log.info("Correo enviado a {} con asunto '{}'", para, asunto);
            return true;
        } catch (Exception e) {
            log.warn("No se pudo enviar correo a {}. Se continúa sin bloquear. Causa: {}",
                    para, e.getMessage());
            return false;
        }
    }
}
