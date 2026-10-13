package com.sarichi.crocheting.integration;

import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.exception.DespachoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TransportadoraService {
    private final TransportadoraConfig config;
    private static final AtomicInteger guiaCounter = new AtomicInteger(1000);
    
    public Map<String, Object> generarGuia(Pedido pedido, String transportadora) throws DespachoException {
        if (!config.isMockEnabled()) {
            return generarGuiaReal(pedido, transportadora);
        }
        
        Map<String, Object> resultado = new HashMap<>();
        String numeroGuia = "GU-" + String.format("%06d", guiaCounter.incrementAndGet());
        
        resultado.put("numeroGuia", numeroGuia);
        resultado.put("enlaceEtiqueta", "https://mock-transportadora.com/label/" + numeroGuia);
        resultado.put("transportadora", transportadora);
        resultado.put("estado", "PENDIENTE");
        resultado.put("fechaEstimadaEntrega", LocalDateTime.now().plusDays(3));
        
        return resultado;
    }
    
    public Map<String, Object> consultarEstado(String numeroGuia) throws DespachoException {
        if (!config.isMockEnabled()) {
            return consultarEstadoReal(numeroGuia);
        }
        
        Map<String, Object> estado = new HashMap<>();
        estado.put("numeroGuia", numeroGuia);
        
        // Mock: alternate between EN_TRANSITO and ENTREGADO based on guid
        int hashCode = Math.abs(numeroGuia.hashCode());
        String estadoSimulado = (hashCode % 2 == 0) ? "EN_TRANSITO" : "ENTREGADO";
        
        estado.put("estado", estadoSimulado);
        estado.put("ultimaActualizacion", "2026-05-26");
        estado.put("ubicacion", "Centro de distribución - Bogotá");
        
        return estado;
    }
    
    public Map<String, Object> obtenerTarifas(String ciudadDestino, Double peso) throws DespachoException {
        if (!config.isMockEnabled()) {
            return obtenerTarifasReal(ciudadDestino, peso);
        }
        
        Map<String, Object> tarifas = new HashMap<>();
        
        // Mock tarifas por ciudad
        Double tarifa = switch (ciudadDestino.toLowerCase()) {
            case "bogota" -> 8.500;
            case "medellin", "cali" -> 12.000;
            case "barranquilla" -> 15.000;
            default -> 20.000;
        };
        
        tarifas.put("tarifa", tarifa);
        tarifas.put("ciudad", ciudadDestino);
        tarifas.put("peso", peso);
        tarifas.put("tiempoEntrega", 3);
        
        return tarifas;
    }
    
    // Mock methods (placeholder for real API calls)
    private Map<String, Object> generarGuiaReal(Pedido pedido, String transportadora) throws DespachoException {
        throw new DespachoException("API real no configurada. Usar mock (app.transportadora.mock-enabled=true)");
    }
    
    private Map<String, Object> consultarEstadoReal(String numeroGuia) throws DespachoException {
        throw new DespachoException("API real no configurada. Usar mock (app.transportadora.mock-enabled=true)");
    }
    
    private Map<String, Object> obtenerTarifasReal(String ciudadDestino, Double peso) throws DespachoException {
        throw new DespachoException("API real no configurada. Usar mock (app.transportadora.mock-enabled=true)");
    }
}
