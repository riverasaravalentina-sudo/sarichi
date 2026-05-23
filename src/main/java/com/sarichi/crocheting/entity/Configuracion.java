package com.sarichi.crocheting.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracion {

    @Id
    private String id;

    @Builder.Default
    private Integer stockMinimoAlerta = 2;

    @Builder.Default
    private Integer umbralVIPCompras = 5;

    @Builder.Default
    private List<ZonaEnvio> zonaEnvio = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ZonaEnvio {
        private String ciudad;
        private Double tarifa;
        private Integer diasEstimados;
    }
}