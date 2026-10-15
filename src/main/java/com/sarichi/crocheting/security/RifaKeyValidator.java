package com.sarichi.crocheting.security;

import com.sarichi.crocheting.exception.ClaveInvalidaException;
import com.sarichi.crocheting.exception.PanelNoConfiguradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Valida la clave del panel de la rifa (variable de entorno RIFA_ADMIN_KEY).
 *
 * Se compara contra el hash SHA-256 de ambas cadenas para no filtrar
 * información por tiempo de comparación.
 */
@Component
public class RifaKeyValidator {

    @Value("${sarichi.rifa.admin-key:}")
    private String claveConfigurada;

    public void validar(String claveRecibida) {
        if (claveConfigurada == null || claveConfigurada.isBlank()) {
            throw new PanelNoConfiguradoException(
                    "El panel de la rifa no está configurado (falta RIFA_ADMIN_KEY)");
        }
        if (claveRecibida == null || claveRecibida.isBlank()
                || !compararSeguro(claveConfigurada, claveRecibida)) {
            throw new ClaveInvalidaException("Clave del panel incorrecta");
        }
    }

    public boolean estaConfigurado() {
        return claveConfigurada != null && !claveConfigurada.isBlank();
    }

    private boolean compararSeguro(String esperada, String recibida) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] a = digest.digest(esperada.getBytes(StandardCharsets.UTF_8));
            byte[] b = digest.digest(recibida.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(a, b);
        } catch (NoSuchAlgorithmException e) {
            return esperada.equals(recibida);
        }
    }
}
