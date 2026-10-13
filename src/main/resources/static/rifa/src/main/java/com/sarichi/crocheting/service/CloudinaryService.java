package com.sarichi.crocheting.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryService(Cloudinary cloudinary,
                             @Value("${cloudinary.cloud.name:}") String cloudName,
                             @Value("${cloudinary.api.key:}") String apiKey,
                             @Value("${cloudinary.api.secret:}") String apiSecret) {
        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public Map<String, Object> subirImagen(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria");
        }

        String carpeta = normalizarCarpeta(folder);
        if (!estaConfigurado()) {
            String nombreSeguro = file.getOriginalFilename() == null
                    ? "imagen"
                    : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "-");
            String publicId = carpeta + "/dev-" + System.currentTimeMillis() + "-" + nombreSeguro;
            return Map.of(
                    "url", "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                    "secureUrl", "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                    "publicId", publicId,
                    "modo", "simulado",
                    "mensaje", "Configura CLOUDINARY_* para subir imágenes reales");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", carpeta,
                            "resource_type", "image"));
            return Map.of(
                    "url", result.get("url"),
                    "secureUrl", result.get("secure_url"),
                    "publicId", result.get("public_id"),
                    "modo", "cloudinary");
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la imagen", e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo subir la imagen a Cloudinary: " + e.getMessage(), e);
        }
    }

    public void eliminarImagen(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("El publicId es obligatorio");
        }
        if (!estaConfigurado()) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo eliminar la imagen de Cloudinary: " + e.getMessage(), e);
        }
    }

    private boolean estaConfigurado() {
        return noVacio(cloudName) && noVacio(apiKey) && noVacio(apiSecret);
    }

    private boolean noVacio(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizarCarpeta(String folder) {
        if (folder == null || folder.isBlank()) {
            return "sarichi/general";
        }
        String limpia = folder.replace("\\", "/").replaceAll("[^a-zA-Z0-9/_-]", "");
        return limpia.startsWith("sarichi/") ? limpia : "sarichi/" + limpia;
    }
}
