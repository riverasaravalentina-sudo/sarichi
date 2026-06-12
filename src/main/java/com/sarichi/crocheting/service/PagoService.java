package com.sarichi.crocheting.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.dto.PagoDTO;
import com.sarichi.crocheting.entity.Pago;
import com.sarichi.crocheting.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final String publicKey;
    private final String baseUrl;

    public PagoService(PagoRepository pagoRepository,
                       @Value("${wompi.public.key:}") String publicKey,
                       @Value("${sarichi.app.base-url:http://localhost:8080/api}") String baseUrl) {
        this.pagoRepository = pagoRepository;
        this.publicKey = publicKey;
        this.baseUrl = baseUrl;
    }

    public PagoDTO iniciarPago(PagoDTO request, String usuarioId) {
        if (request.getPedidoId() == null || request.getPedidoId().isBlank()) {
            throw new IllegalArgumentException("El pedidoId es obligatorio");
        }
        if (request.getMonto() == null || request.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        String referencia = "SARICHI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String redirectUrl = construirRedirectUrl(referencia, request.getMonto());

        Pago pago = Pago.builder()
                .pedidoId(request.getPedidoId())
                .usuarioId(usuarioId)
                .monto(request.getMonto())
                .moneda("COP")
                .metodoPago(request.getMetodoPago() == null ? "CARD" : request.getMetodoPago())
                .referencia(referencia)
                .estado("PENDIENTE")
                .proveedor(estaConfigurado() ? "WOMPI" : "WOMPI_MOCK")
                .redirectUrl(redirectUrl)
                .build();

        Pago guardado = pagoRepository.save(pago);
        return PagoDTO.builder()
                .pedidoId(guardado.getPedidoId())
                .monto(guardado.getMonto())
                .metodoPago(guardado.getMetodoPago())
                .referencia(guardado.getReferencia())
                .estado(guardado.getEstado())
                .redirectUrl(guardado.getRedirectUrl())
                .build();
    }

    public void procesarWebhook(Map<String, Object> payload) {
        String referencia = extraerReferencia(payload);
        if (referencia == null || referencia.isBlank()) {
            return;
        }
        pagoRepository.findByReferencia(referencia).ifPresent(pago -> {
            pago.setEstado(extraerEstado(payload));
            pago.setFechaActualizacion(LocalDateTime.now());
            pagoRepository.save(pago);
        });
    }

    private String construirRedirectUrl(String referencia, Double monto) {
        if (!estaConfigurado()) {
            return baseUrl + "/web/mis-pedidos?pago=mock&referencia=" + referencia;
        }

        long centavos = Math.round(monto * 100);
        String redirect = baseUrl + "/web/mis-pedidos?pago=wompi&referencia=" + referencia;
        return "https://checkout.wompi.co/p/?public-key=" + encode(publicKey)
                + "&currency=COP"
                + "&amount-in-cents=" + centavos
                + "&reference=" + encode(referencia)
                + "&redirect-url=" + encode(redirect);
    }

    private boolean estaConfigurado() {
        return publicKey != null && !publicKey.isBlank();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private String extraerReferencia(Map<String, Object> payload) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object transaction = dataMap.get("transaction");
            if (transaction instanceof Map<?, ?> transactionMap) {
                Object reference = transactionMap.get("reference");
                return reference == null ? null : reference.toString();
            }
        }
        Object reference = payload.get("reference");
        return reference == null ? null : reference.toString();
    }

    @SuppressWarnings("unchecked")
    private String extraerEstado(Map<String, Object> payload) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object transaction = dataMap.get("transaction");
            if (transaction instanceof Map<?, ?> transactionMap) {
                Object status = transactionMap.get("status");
                return status == null ? "ACTUALIZADO" : status.toString();
            }
        }
        Object status = payload.get("status");
        return status == null ? "ACTUALIZADO" : status.toString();
    }
}
