package com.sarichi.crocheting.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.entity.Configuracion;
import com.sarichi.crocheting.repository.ConfiguracionRepository;

/**
 * Servicio para manejar la configuración global de la aplicación.
 */
@Service
public class ConfiguracionService {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracionService.class);

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    // Obtener la configuración actual; crear una por defecto si no existe.
    public Configuracion obtenerConfiguracion() {
        log.info("Obteniendo configuración");
        Optional<Configuracion> opt = configuracionRepository.findFirstBy();
        if (opt.isPresent()) return opt.get();

        // Crear configuración por defecto
        Configuracion defecto = Configuracion.builder()
                .stockMinimoAlerta(2)
                .umbralVIPCompras(5)
                .zonaEnvio(java.util.Collections.emptyList())
                .build();
        Configuracion saved = configuracionRepository.save(defecto);
        log.info("Configuración por defecto creada con id={}", saved.getId());
        return saved;
    }

    // Actualizar la configuración existente (o crear si no hay)
    public Configuracion actualizarConfiguracion(Configuracion nueva) {
        log.info("Actualizando configuración");
        Optional<Configuracion> opt = configuracionRepository.findFirstBy();
        if (opt.isPresent()) {
            Configuracion actual = opt.get();
            actual.setStockMinimoAlerta(nueva.getStockMinimoAlerta());
            actual.setUmbralVIPCompras(nueva.getUmbralVIPCompras());
            actual.setZonaEnvio(nueva.getZonaEnvio());
            return configuracionRepository.save(actual);
        }
        return configuracionRepository.save(nueva);
    }
}