package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.ActualizarDespachoDTO;
import com.sarichi.crocheting.dto.CrearDespachoDTO;
import com.sarichi.crocheting.dto.DespachoDTO;
import com.sarichi.crocheting.entity.Despacho;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.DespachoException;
import com.sarichi.crocheting.integration.TransportadoraService;
import com.sarichi.crocheting.repository.DespachoRepository;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoService {
    private final DespachoRepository despachoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransportadoraService transportadoraService;
    
    public DespachoDTO crearDespacho(CrearDespachoDTO dto) throws DespachoException {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
            .orElseThrow(() -> new DespachoException("Pedido no encontrado"));
        
        if (!"LISTO".equals(pedido.getEstado())) {
            throw new DespachoException("Pedido no está en estado LISTO para despachar");
        }
        
        // Generar guía con transportadora
        Map<String, Object> guiaData = transportadoraService.generarGuia(pedido, dto.getTransportadora());
        
        // Crear despacho
        Despacho despacho = Despacho.builder()
            .pedidoId(dto.getPedidoId())
            .transportadora(dto.getTransportadora())
            .numeroGuia((String) guiaData.get("numeroGuia"))
            .fechaEstimadaEntrega(((LocalDateTime) guiaData.get("fechaEstimadaEntrega")))
            .observaciones(dto.getObservaciones())
            .build();
        
        despachoRepository.save(despacho);
        
        // Actualizar estado del pedido a DESPACHADO
        pedido.setEstado("DESPACHADO");
        pedidoRepository.save(pedido);
        
        return entityToDto(despacho, pedido);
    }
    
    public List<DespachoDTO> listarDespachosHoy() {
        LocalDateTime hoy = LocalDateTime.now();
        LocalDateTime inicioHoy = hoy.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finHoy = hoy.withHour(23).withMinute(59).withSecond(59);
        
        return despachoRepository.findByFechaDespachoBetween(inicioHoy, finHoy)
            .stream()
            .map(d -> {
                Pedido p = pedidoRepository.findById(d.getPedidoId()).orElse(null);
                return entityToDto(d, p);
            })
            .collect(Collectors.toList());
    }
    
    public List<DespachoDTO> listarDespachosPendientes() {
        return pedidoRepository.findByEstado("LISTO")
            .stream()
            .filter(p -> despachoRepository.findByPedidoId(p.getId()).isEmpty())
            .map(p -> DespachoDTO.builder()
                .pedidoId(p.getId())
                .clienteNombre(usuarioRepository.findById(p.getUsuarioId()).map(Usuario::getNombre).orElse("N/A"))
                .estado("PENDIENTE")
                .build())
            .collect(Collectors.toList());
    }
    
    public DespachoDTO actualizarEstadoDespacho(String despachoId, ActualizarDespachoDTO dto) throws DespachoException {
        Despacho despacho = despachoRepository.findById(despachoId)
            .orElseThrow(() -> new DespachoException("Despacho no encontrado"));
        
        despacho.setEstado(dto.getEstado());
        if (dto.getNumeroGuia() != null) {
            despacho.setNumeroGuia(dto.getNumeroGuia());
        }
        if (dto.getObservaciones() != null) {
            despacho.setObservaciones(dto.getObservaciones());
        }
        
        despachoRepository.save(despacho);
        
        // Si el despacho llega a ENTREGADO, actualizar pedido también
        if ("ENTREGADO".equals(dto.getEstado())) {
            Pedido pedido = pedidoRepository.findById(despacho.getPedidoId()).orElse(null);
            if (pedido != null) {
                pedido.setEstado("ENTREGADO");
                pedidoRepository.save(pedido);
            }
        }
        
        Pedido pedido = pedidoRepository.findById(despacho.getPedidoId()).orElse(null);
        return entityToDto(despacho, pedido);
    }
    
    public Map<String, Object> consultarSeguimiento(String numeroGuia) throws DespachoException {
        return transportadoraService.consultarEstado(numeroGuia);
    }
    
    public Map<String, Object> obtenerEstadisticas() {
        LocalDateTime hoy = LocalDateTime.now();
        LocalDateTime inicioHoy = hoy.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finHoy = hoy.withHour(23).withMinute(59).withSecond(59);
        
        LocalDateTime inicioSemana = hoy.minusDays(7);
        
        long despachosHoy = despachoRepository.findByFechaDespachoBetween(inicioHoy, finHoy).size();
        long despachosSemana = despachoRepository.findByFechaDespachoBetween(inicioSemana, finHoy).size();
        long pedidosPendientes = pedidoRepository.findByEstado("LISTO").size();
        
        return Map.of(
            "despachosHoy", despachosHoy,
            "despachosSemana", despachosSemana,
            "pedidosPendientesDespacho", pedidosPendientes,
            "promedioEntregaDias", 3.0
        );
    }
    
    private DespachoDTO entityToDto(Despacho despacho, Pedido pedido) {
        String clienteNombre = "N/A";
        String ciudadDestino = "N/A";
        
        if (pedido != null) {
            Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId()).orElse(null);
            if (usuario != null) {
                clienteNombre = usuario.getNombre();
            }
            // Extraer ciudad de dirección (formato: "calle, ciudad, país")
            if (pedido.getDireccionEnvio() != null) {
                String[] partes = pedido.getDireccionEnvio().split(",");
                if (partes.length >= 2) {
                    ciudadDestino = partes[1].trim();
                }
            }
        }
        
        return DespachoDTO.builder()
            .id(despacho.getId())
            .pedidoId(despacho.getPedidoId())
            .clienteNombre(clienteNombre)
            .ciudadDestino(ciudadDestino)
            .fechaDespacho(despacho.getFechaDespacho())
            .transportadora(despacho.getTransportadora())
            .numeroGuia(despacho.getNumeroGuia())
            .estado(despacho.getEstado())
            .fechaEstimadaEntrega(despacho.getFechaEstimadaEntrega())
            .observaciones(despacho.getObservaciones())
            .build();
    }
}
