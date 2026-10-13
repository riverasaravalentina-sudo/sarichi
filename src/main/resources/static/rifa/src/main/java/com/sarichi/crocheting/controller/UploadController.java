package com.sarichi.crocheting.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sarichi.crocheting.service.CloudinaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Subida de imágenes a Cloudinary")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value = "/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','ARTESANA','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Subir imagen a Cloudinary",
            description = "Folders sugeridos: productos, galeria, proceso, blog")
    public ResponseEntity<Map<String, Object>> subirImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {
        return ResponseEntity.ok(cloudinaryService.subirImagen(file, folder));
    }

    @DeleteMapping("/imagen")
    @PreAuthorize("hasAnyRole('ADMIN','ARTESANA','MERCADEO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar imagen de Cloudinary por publicId")
    public ResponseEntity<Map<String, String>> eliminarImagen(@RequestParam String publicId) {
        cloudinaryService.eliminarImagen(publicId);
        return ResponseEntity.ok(Map.of("mensaje", "Imagen eliminada correctamente"));
    }
}
