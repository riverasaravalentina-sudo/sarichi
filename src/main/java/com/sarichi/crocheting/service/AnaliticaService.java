package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.MetricasTraficoDTO;
import com.sarichi.crocheting.entity.EventoTrafico;
import com.sarichi.crocheting.repository.EventoTraficoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnaliticaService {
    
    private final EventoTraficoRepository eventoRepository;
    
    public void registrarVisita(String sessionId, String fuente, String url, String usuarioId, String userAgent, String ipAddress) {
        EventoTrafico evento = EventoTrafico.builder()
                .fecha(LocalDateTime.now())
                .tipo("VISITA")
                .fuente(fuente != null ? fuente : "DIRECTO")
                .url(url)
                .sessionId(sessionId)
                .usuarioId(usuarioId)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();
        
        eventoRepository.save(evento);
    }
    
    public void registrarClick(String sessionId, String url, String productoId) {
        EventoTrafico evento = EventoTrafico.builder()
                .fecha(LocalDateTime.now())
                .tipo("CLICK")
                .fuente("INTERNO")
                .url(url)
                .sessionId(sessionId)
                .build();
        
        eventoRepository.save(evento);
    }
    
    public void registrarConversion(String sessionId, String pedidoId, Double valor) {
        EventoTrafico evento = EventoTrafico.builder()
                .fecha(LocalDateTime.now())
                .tipo("CONVERSION")
                .fuente("INTERNO")
                .sessionId(sessionId)
                .pedidoId(pedidoId)
                .valorConversion(valor)
                .build();
        
        eventoRepository.save(evento);
    }
    
    public MetricasTraficoDTO obtenerMetricasTrafico(LocalDateTime desde, LocalDateTime hasta) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hoyComienzo = ahora.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime hoyFin = ahora.withHour(23).withMinute(59).withSecond(59);
        
        LocalDateTime semanaComienzo = ahora.minusDays(7);
        
        Long visitasHoy = eventoRepository.countByTipoAndFechaBetween("VISITA", hoyComienzo, hoyFin);
        Long visitasSemana = eventoRepository.countByTipoAndFechaBetween("VISITA", semanaComienzo, ahora);
        Long visitasMes = eventoRepository.countByFechaBetween(desde, hasta);
        
        Long conversionesTotal = eventoRepository.countByTipoAndFechaBetween("CONVERSION", desde, hasta);
        Double tasaConversion = visitasMes > 0 ? (conversionesTotal * 100.0) / visitasMes : 0.0;
        
        Map<String, Long> visitasPorFuente = agruparVisitasPorFuente(desde, hasta);
        
        return MetricasTraficoDTO.builder()
                .visitasHoy(visitasHoy)
                .visitasSemana(visitasSemana)
                .visitasMes(visitasMes)
                .visitasPorFuente(visitasPorFuente)
                .tasaConversion(tasaConversion)
                .conversionesHoy(eventoRepository.countByTipoAndFechaBetween("CONVERSION", hoyComienzo, hoyFin))
                .conversionesSemana(eventoRepository.countByTipoAndFechaBetween("CONVERSION", semanaComienzo, ahora))
                .build();
    }
    
    private Map<String, Long> agruparVisitasPorFuente(LocalDateTime desde, LocalDateTime hasta) {
        Map<String, Long> fuentes = new HashMap<>();
        
        List<String> fuentesList = List.of("INSTAGRAM", "GOOGLE", "DIRECTO", "WHATSAPP");
        for (String fuente : fuentesList) {
            Long count = eventoRepository.countByFuenteAndFechaBetween(fuente, desde, hasta);
            fuentes.put(fuente, count);
        }
        
        return fuentes;
    }
    
    public List<String> obtenerPaginasMasVisitadas(LocalDateTime desde, LocalDateTime hasta, int limit) {
        return eventoRepository.findByFechaBetween(desde, hasta)
                .stream()
                .filter(e -> "VISITA".equals(e.getTipo()))
                .map(EventoTrafico::getUrl)
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
