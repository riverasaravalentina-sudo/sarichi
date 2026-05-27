package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasTraficoDTO {
    private Long visitasHoy;
    private Long visitasSemana;
    private Long visitasMes;
    private Map<String, Long> visitasPorFuente;
    private Double tasaConversion;
    private List<String> paginasMasVisitadas;
    private Long conversionesHoy;
    private Long conversionesSemana;
}
