package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.DevolucionDTO;
import com.sarichi.crocheting.dto.SolicitarDevolucionDTO;
import com.sarichi.crocheting.entity.Devolucion;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.DevolucionException;
import com.sarichi.crocheting.repository.DevolucionRepository;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevolucionService {
    private final DevolucionRepository devolucionRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;
    
    private static final int DIAS_MAXIMOS_DEVOLUCION = 15;
    
    public DevolucionDTO solicitarDevolucion(String usuarioId, SolicitarDevolucionDTO dto) throws DevolucionException {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
            .orElseThrow(() -> new DevolucionException("Pedido no encontrado"));
        
        if (!"ENTREGADO".equals(pedido.getEstado())) {
            throw new DevolucionException("Solo se pueden devolver pedidos entregados");
        }
        
        // Validar plazo de devolución (15 días desde fecha de entrega estimada)
        LocalDateTime fechaLimiteDevolución = pedido.getFechaEntregaEstimada() != null 
            ? pedido.getFechaEntregaEstimada().plusDays(DIAS_MAXIMOS_DEVOLUCION)
            : LocalDateTime.now();
        
        if (LocalDateTime.now().isAfter(fechaLimiteDevolución)) {
            throw new DevolucionException("El plazo máximo para devolver es de " + DIAS_MAXIMOS_DEVOLUCION + " días");
        }
        
        // Verificar si ya existe devolución para este pedido
        if (devolucionRepository.findByPedidoId(dto.getPedidoId()).isPresent()) {
            throw new DevolucionException("Ya existe una devolución para este pedido");
        }
        
        Devolucion devolucion = Devolucion.builder()
            .pedidoId(dto.getPedidoId())
            .usuarioId(usuarioId)
            .motivo(dto.getMotivo())
            .estado("SOLICITADA")
            .build();
        
        devolucionRepository.save(devolucion);
        return entityToDto(devolucion);
    }
    
    public List<DevolucionDTO> listarMisDevoluciones(String usuarioId) {
        return devolucionRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }
    
    public List<DevolucionDTO> listarTodasDevoluciones() {
        return devolucionRepository.findAll()
            .stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }
    
    public DevolucionDTO aprobarDevolucion(String devolucionId, String observaciones) throws DevolucionException {
        Devolucion devolucion = devolucionRepository.findById(devolucionId)
            .orElseThrow(() -> new DevolucionException("Devolución no encontrada"));
        
        devolucion.setEstado("APROBADA");
        devolucion.setFechaAprobacion(LocalDateTime.now());
        devolucion.setObservacionesAdmin(observaciones);
        
        devolucionRepository.save(devolucion);
        return entityToDto(devolucion);
    }
    
    public DevolucionDTO rechazarDevolucion(String devolucionId, String observaciones) throws DevolucionException {
        Devolucion devolucion = devolucionRepository.findById(devolucionId)
            .orElseThrow(() -> new DevolucionException("Devolución no encontrada"));
        
        devolucion.setEstado("RECHAZADA");
        devolucion.setObservacionesAdmin(observaciones);
        
        devolucionRepository.save(devolucion);
        return entityToDto(devolucion);
    }
    
    public DevolucionDTO registrarRecepcion(String devolucionId) throws DevolucionException {
        Devolucion devolucion = devolucionRepository.findById(devolucionId)
            .orElseThrow(() -> new DevolucionException("Devolución no encontrada"));
        
        if (!"APROBADA".equals(devolucion.getEstado())) {
            throw new DevolucionException("La devolución debe estar aprobada para registrar recepción");
        }
        
        devolucion.setEstado("COMPLETADA");
        devolucion.setFechaRecepcion(LocalDateTime.now());
        
        devolucionRepository.save(devolucion);
        
        // TODO: Recuperar stock del producto cuando se reciba la devolución
        // Por ahora solo registramos el estado
        
        return entityToDto(devolucion);
    }
    
    private DevolucionDTO entityToDto(Devolucion devolucion) {
        Usuario usuario = usuarioRepository.findById(devolucion.getUsuarioId()).orElse(null);
        
        return DevolucionDTO.builder()
            .id(devolucion.getId())
            .pedidoId(devolucion.getPedidoId())
            .usuarioId(devolucion.getUsuarioId())
            .usuarioNombre(usuario != null ? usuario.getNombre() : "N/A")
            .motivo(devolucion.getMotivo())
            .estado(devolucion.getEstado())
            .fechaSolicitud(devolucion.getFechaSolicitud())
            .fechaAprobacion(devolucion.getFechaAprobacion())
            .observacionesAdmin(devolucion.getObservacionesAdmin())
            .build();
    }
}
