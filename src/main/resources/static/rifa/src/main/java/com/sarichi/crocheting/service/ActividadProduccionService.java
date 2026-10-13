package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.ActividadProduccionDTO;
import com.sarichi.crocheting.dto.FotoProcesoDTO;
import com.sarichi.crocheting.entity.ActividadProduccion;
import com.sarichi.crocheting.entity.FotoProceso;
import com.sarichi.crocheting.entity.Pedido;
import com.sarichi.crocheting.entity.Producto;
import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.exception.ProduccionException;
import com.sarichi.crocheting.repository.ActividadProduccionRepository;
import com.sarichi.crocheting.repository.PedidoRepository;
import com.sarichi.crocheting.repository.ProductoRepository;
import com.sarichi.crocheting.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadProduccionService {
    private final ActividadProduccionRepository actividadRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    private static final int MAX_ACTIVIDADES_ARTESANA = 5;
    
    public ActividadProduccionDTO crearActividad(String pedidoId, String artesanaId) throws ProduccionException {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ProduccionException("Pedido no encontrado"));
        
        if (!"EN_PRODUCCION".equals(pedido.getEstado())) {
            throw new ProduccionException("Pedido no está en estado EN_PRODUCCION");
        }
        
        // Validar capacidad de artesana
        long actividadesActivas = actividadRepository.countByArtesanaIdAndEstado(artesanaId, "EN_PROGRESO");
        if (actividadesActivas >= MAX_ACTIVIDADES_ARTESANA) {
            throw new ProduccionException("Artesana ha alcanzado capacidad máxima de " + MAX_ACTIVIDADES_ARTESANA + " actividades");
        }
        
        // Usar primer item del pedido como referencia
        String productoId = pedido.getItems().get(0).getProductoId();
        Integer cantidad = pedido.getItems().get(0).getCantidad();
        
        ActividadProduccion actividad = ActividadProduccion.builder()
            .pedidoId(pedidoId)
            .productoId(productoId)
            .artesanaId(artesanaId)
            .cantidad(cantidad)
            .estado("PENDIENTE")
            .fotos(new ArrayList<>())
            .build();
        
        actividadRepository.save(actividad);
        return entityToDto(actividad);
    }
    
    public List<ActividadProduccionDTO> listarMisActividades(String artesanaId) {
        return actividadRepository.findByArtesanaId(artesanaId)
            .stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }
    
    public List<ActividadProduccionDTO> listarActividadesPorEstado(String estado) {
        return actividadRepository.findByEstado(estado)
            .stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
    }
    
    public ActividadProduccionDTO iniciarActividad(String actividadId) throws ProduccionException {
        ActividadProduccion actividad = actividadRepository.findById(actividadId)
            .orElseThrow(() -> new ProduccionException("Actividad no encontrada"));
        
        if (!"PENDIENTE".equals(actividad.getEstado())) {
            throw new ProduccionException("Solo se pueden iniciar actividades en estado PENDIENTE");
        }
        
        actividad.setEstado("EN_PROGRESO");
        actividad.setFechaInicio(LocalDateTime.now());
        
        actividadRepository.save(actividad);
        return entityToDto(actividad);
    }
    
    public ActividadProduccionDTO completarActividad(String actividadId) throws ProduccionException {
        ActividadProduccion actividad = actividadRepository.findById(actividadId)
            .orElseThrow(() -> new ProduccionException("Actividad no encontrada"));
        
        if (!"EN_PROGRESO".equals(actividad.getEstado())) {
            throw new ProduccionException("Solo se pueden completar actividades EN_PROGRESO");
        }
        
        actividad.setEstado("COMPLETADO");
        actividad.setFechaFinalizacion(LocalDateTime.now());
        
        actividadRepository.save(actividad);
        
        // Cambiar pedido a LISTO
        Pedido pedido = pedidoRepository.findById(actividad.getPedidoId()).orElse(null);
        if (pedido != null) {
            pedido.setEstado("LISTO");
            pedidoRepository.save(pedido);
        }
        
        return entityToDto(actividad);
    }
    
    public ActividadProduccionDTO subirFotoProceso(String actividadId, FotoProcesoDTO fotoDTO) throws ProduccionException {
        ActividadProduccion actividad = actividadRepository.findById(actividadId)
            .orElseThrow(() -> new ProduccionException("Actividad no encontrada"));
        
        FotoProceso foto = FotoProceso.builder()
            .id(UUID.randomUUID().toString())
            .urlFoto(fotoDTO.getUrlFoto())
            .descripcion(fotoDTO.getDescripcion())
            .orden(actividad.getFotos().size() + 1)
            .build();
        
        actividad.getFotos().add(foto);
        actividadRepository.save(actividad);
        
        return entityToDto(actividad);
    }
    
    public long obtenerCapacidadArtesana(String artesanaId) {
        return actividadRepository.countByArtesanaIdAndEstado(artesanaId, "EN_PROGRESO");
    }
    
    private ActividadProduccionDTO entityToDto(ActividadProduccion actividad) {
        Pedido pedido = pedidoRepository.findById(actividad.getPedidoId()).orElse(null);
        Producto producto = productoRepository.findById(actividad.getProductoId()).orElse(null);
        Usuario artesana = usuarioRepository.findById(actividad.getArtesanaId()).orElse(null);
        
        List<FotoProcesoDTO> fotosDTO = actividad.getFotos() != null 
            ? actividad.getFotos().stream()
                .map(f -> FotoProcesoDTO.builder()
                    .id(f.getId())
                    .urlFoto(f.getUrlFoto())
                    .descripcion(f.getDescripcion())
                    .fechaSubida(f.getFechaSubida())
                    .orden(f.getOrden())
                    .build())
                .collect(Collectors.toList())
            : new ArrayList<>();
        
        return ActividadProduccionDTO.builder()
            .id(actividad.getId())
            .pedidoId(actividad.getPedidoId())
            .pedidoNumero(pedido != null ? pedido.getId() : "N/A")
            .productoId(actividad.getProductoId())
            .productoNombre(producto != null ? producto.getNombre() : "N/A")
            .artesanaId(actividad.getArtesanaId())
            .artesanaNombre(artesana != null ? artesana.getNombre() : "N/A")
            .cantidad(actividad.getCantidad())
            .fechaInicio(actividad.getFechaInicio())
            .fechaFinalizacion(actividad.getFechaFinalizacion())
            .estado(actividad.getEstado())
            .notas(actividad.getNotas())
            .fotos(fotosDTO)
            .build();
    }
}
