package com.sarichi.crocheting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void enviarCorreoRecuperacion(String destinatario,
                                         String nombre,
                                         String token) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Sarichi Crocheting — Recupera tu contrasena");

            String enlace = frontendUrl + "/restablecer.html?token=" + token;

            String html = "<html><body style='font-family:Arial,sans-serif;"
                + "background:#FDFAF6;padding:20px;'>"
                + "<div style='max-width:560px;margin:auto;background:#fff;"
                + "border-radius:12px;border-top:4px solid #E8527A;padding:32px;'>"
                + "<h2 style='color:#E8527A;'>Crocheting Sarichi</h2>"
                + "<p>Hola <strong>" + nombre + "</strong>,</p>"
                + "<p>Recibimos una solicitud para restablecer tu contrasena.</p>"
                + "<a href='" + enlace + "' style='display:inline-block;"
                + "background:#E8527A;color:#fff;padding:12px 24px;"
                + "border-radius:8px;text-decoration:none;font-weight:bold;'>"
                + "Restablecer contrasena</a>"
                + "<p style='color:#888;font-size:13px;margin-top:16px;'>"
                + "Este enlace expira en <strong>15 minutos</strong>. "
                + "Si no solicitaste este cambio, ignora este correo.</p>"
                + "</div></body></html>";

            helper.setText(html, true);
            mailSender.send(mensaje);
            log.info("Correo de recuperacion enviado a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error enviando correo a {}: {}", destinatario, e.getMessage());
        }
    }

    public void enviarBienvenida(String destinatario, String nombre) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject("Bienvenida a Crocheting Sarichi!");
            mensaje.setText("Hola " + nombre + ",\n\n"
                + "Gracias por unirte a Crocheting Sarichi!\n"
                + "Ya puedes explorar nuestro catalogo.\n\n"
                + "Con amor artesanal,\n"
                + "El equipo de Sarichi");
            mailSender.send(mensaje);
            log.info("Correo de bienvenida enviado a: {}", destinatario);
        } catch (Exception e) {
            log.warn("No se pudo enviar bienvenida a {}: {}", destinatario, e.getMessage());
        }
    }
}