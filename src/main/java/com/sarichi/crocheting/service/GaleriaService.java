package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.ColeccionGaleriaDTO;
import com.sarichi.crocheting.dto.FotoGaleriaDTO;
import com.sarichi.crocheting.entity.ColeccionGaleria;
import com.sarichi.crocheting.entity.FotoGaleria;
import com.sarichi.crocheting.exception.BlogException;
import com.sarichi.crocheting.repository.ColeccionGaleriaRepository;
import com.sarichi.crocheting.repository.FotoGaleriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GaleriaService {
    
    private final ColeccionGaleriaRepository coleccionRepository;
    private final FotoGaleriaRepository fotoRepository;
    
    public ColeccionGaleriaDTO crearColeccion(String nombre, String descripcion, String portadaUrl) {
        String slug = generarSlug(nombre);
        
        ColeccionGaleria coleccion = ColeccionGaleria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .slug(slug)
                .portadaUrl(portadaUrl)
                .orden((int) (coleccionRepository.count() + 1))
                .estado("ACTIVA")
                .fechaCreacion(LocalDateTime.now())
                .build();
        
        return mapColeccionToDTO(coleccionRepository.save(coleccion));
    }
    
    public List<ColeccionGaleriaDTO> listarColeccionesActivas() {
        return coleccionRepository.findByEstadoOrderByOrdenAsc("ACTIVA")
                .stream()
                .map(this::mapColeccionToDTO)
                .collect(Collectors.toList());
    }
    
    public ColeccionGaleriaDTO obtenerColeccionPorSlug(String slug) {
        ColeccionGaleria coleccion = coleccionRepository.findBySlug(slug)
                .orElseThrow(() -> new BlogException("Colección no encontrada"));
        
        return mapColeccionToDTO(coleccion);
    }
    
    public FotoGaleriaDTO agregarFotoAColeccion(String coleccionId, String urlFoto, String titulo, String descripcion, String historia, Integer tiempoElaboracionHoras) {
        ColeccionGaleria coleccion = coleccionRepository.findById(coleccionId)
                .orElseThrow(() -> new BlogException("Colección no encontrada"));
        
        FotoGaleria foto = FotoGaleria.builder()
                .idColeccion(coleccionId)
                .urlFoto(urlFoto)
                .titulo(titulo)
                .descripcion(descripcion)
                .historia(historia)
                .tiempoElaboracionHoras(tiempoElaboracionHoras)
                .fechaPublicacion(LocalDateTime.now())
                .orden((int) (fotoRepository.countByIdColeccion(coleccionId) + 1))
                .likes(0L)
                .compartidoEnInstagram(false)
                .build();
        
        return mapFotoToDTO(fotoRepository.save(foto));
    }
    
    public List<FotoGaleriaDTO> listarFotosPorColeccion(String coleccionId) {
        return fotoRepository.findByIdColeccionOrderByOrdenAsc(coleccionId)
                .stream()
                .map(this::mapFotoToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FotoGaleriaDTO> listarFotosRecientes(int limit) {
        return fotoRepository.findTop6ByOrderByFechaPublicacionDesc()
                .stream()
                .limit(limit)
                .map(this::mapFotoToDTO)
                .collect(Collectors.toList());
    }
    
    public FotoGaleriaDTO vincularFotoAProducto(String fotoId, String productoId) {
        FotoGaleria foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new BlogException("Foto no encontrada"));
        
        foto.setIdProducto(productoId);
        return mapFotoToDTO(fotoRepository.save(foto));
    }
    
    public void eliminarFoto(String fotoId) {
        fotoRepository.deleteById(fotoId);
    }
    
    public FotoGaleriaDTO darLikeAFoto(String fotoId) {
        FotoGaleria foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new BlogException("Foto no encontrada"));
        
        foto.setLikes(foto.getLikes() + 1);
        return mapFotoToDTO(fotoRepository.save(foto));
    }
    
    private String generarSlug(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
    
    private ColeccionGaleriaDTO mapColeccionToDTO(ColeccionGaleria coleccion) {
        return ColeccionGaleriaDTO.builder()
                .id(coleccion.getId())
                .nombre(coleccion.getNombre())
                .descripcion(coleccion.getDescripcion())
                .slug(coleccion.getSlug())
                .portadaUrl(coleccion.getPortadaUrl())
                .estado(coleccion.getEstado())
                .build();
    }
    
    private FotoGaleriaDTO mapFotoToDTO(FotoGaleria foto) {
        return FotoGaleriaDTO.builder()
                .id(foto.getId())
                .idColeccion(foto.getIdColeccion())
                .idProducto(foto.getIdProducto())
                .urlFoto(foto.getUrlFoto())
                .titulo(foto.getTitulo())
                .descripcion(foto.getDescripcion())
                .historia(foto.getHistoria())
                .tiempoElaboracionHoras(foto.getTiempoElaboracionHoras())
                .likes(foto.getLikes())
                .build();
    }
}
