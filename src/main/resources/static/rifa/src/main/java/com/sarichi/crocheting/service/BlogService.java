package com.sarichi.crocheting.service;

import com.sarichi.crocheting.dto.ArticuloBlogDTO;
import com.sarichi.crocheting.dto.CrearArticuloDTO;
import com.sarichi.crocheting.entity.ArticuloBlog;
import com.sarichi.crocheting.exception.BlogException;
import com.sarichi.crocheting.repository.ArticuloBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {
    
    private final ArticuloBlogRepository repository;
    
    public ArticuloBlogDTO crearArticulo(CrearArticuloDTO dto, String autorId, String autorNombre) {
        String slug = generarSlugUnico(dto.getTitulo());
        
        ArticuloBlog articulo = ArticuloBlog.builder()
                .titulo(dto.getTitulo())
                .slug(slug)
                .contenidoHtml(dto.getContenidoHtml())
                .resumen(dto.getResumen())
                .categorias(dto.getCategorias())
                .etiquetas(dto.getEtiquetas())
                .idAutor(autorId)
                .autorNombre(autorNombre)
                .estado("BORRADOR")
                .metaTituloSeo(dto.getMetaTituloSeo())
                .metaDescripcionSeo(dto.getMetaDescripcionSeo())
                .imagenPrincipalUrl(dto.getImagenPrincipalUrl())
                .tiempoLecturaMinutos(dto.getTiempoLecturaMinutos())
                .visitas(0L)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        return mapToDTO(repository.save(articulo));
    }
    
    public ArticuloBlogDTO publicarArticulo(String id) {
        ArticuloBlog articulo = repository.findById(id)
                .orElseThrow(() -> new BlogException("Artículo no encontrado"));
        
        articulo.setEstado("PUBLICADO");
        articulo.setFechaPublicacion(LocalDateTime.now());
        articulo.setFechaActualizacion(LocalDateTime.now());
        
        return mapToDTO(repository.save(articulo));
    }
    
    public ArticuloBlogDTO obtenerPorSlug(String slug) {
        ArticuloBlog articulo = repository.findBySlug(slug)
                .orElseThrow(() -> new BlogException("Artículo no encontrado"));
        
        // Incrementar visitas
        articulo.setVisitas(articulo.getVisitas() + 1);
        repository.save(articulo);
        
        return mapToDTO(articulo);
    }
    
    public List<ArticuloBlogDTO> listarPublicados() {
        return repository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADO")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticuloBlogDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public ArticuloBlogDTO actualizarArticulo(String id, CrearArticuloDTO dto) {
        ArticuloBlog articulo = repository.findById(id)
                .orElseThrow(() -> new BlogException("Artículo no encontrado"));
        
        articulo.setTitulo(dto.getTitulo());
        articulo.setContenidoHtml(dto.getContenidoHtml());
        articulo.setResumen(dto.getResumen());
        articulo.setCategorias(dto.getCategorias());
        articulo.setEtiquetas(dto.getEtiquetas());
        articulo.setMetaTituloSeo(dto.getMetaTituloSeo());
        articulo.setMetaDescripcionSeo(dto.getMetaDescripcionSeo());
        articulo.setImagenPrincipalUrl(dto.getImagenPrincipalUrl());
        articulo.setFechaActualizacion(LocalDateTime.now());
        
        return mapToDTO(repository.save(articulo));
    }
    
    public void eliminarArticulo(String id) {
        ArticuloBlog articulo = repository.findById(id)
                .orElseThrow(() -> new BlogException("Artículo no encontrado"));
        
        articulo.setEstado("ARCHIVADO");
        repository.save(articulo);
    }
    
    public List<ArticuloBlogDTO> buscarPorCategoria(String categoria) {
        return repository.findByCategoriasContaining(categoria)
                .stream()
                .filter(a -> "PUBLICADO".equals(a.getEstado()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ArticuloBlogDTO> obtenerArticulosRecientes(int limit) {
        return repository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADO")
                .stream()
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private String generarSlugUnico(String titulo) {
        String slug = titulo.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        
        int contador = 1;
        String slugFinal = slug;
        while (repository.findBySlug(slugFinal).isPresent()) {
            slugFinal = slug + "-" + contador++;
        }
        return slugFinal;
    }
    
    private ArticuloBlogDTO mapToDTO(ArticuloBlog articulo) {
        return ArticuloBlogDTO.builder()
                .id(articulo.getId())
                .titulo(articulo.getTitulo())
                .slug(articulo.getSlug())
                .contenidoHtml(articulo.getContenidoHtml())
                .resumen(articulo.getResumen())
                .categorias(articulo.getCategorias())
                .etiquetas(articulo.getEtiquetas())
                .autorNombre(articulo.getAutorNombre())
                .estado(articulo.getEstado())
                .fechaPublicacion(articulo.getFechaPublicacion())
                .visitas(articulo.getVisitas())
                .tiempoLectura(articulo.getTiempoLecturaMinutos())
                .imagenPrincipalUrl(articulo.getImagenPrincipalUrl())
                .build();
    }
}
