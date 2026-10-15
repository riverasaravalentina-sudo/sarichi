package com.sarichi.crocheting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/** Resultado de una reserva (POST /api/rifa/reservar). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRifaResponseDTO {
    private String codigoReserva;
    private List<Integer> numeros;
    private int total;
    private LocalDateTime expiraEn;
}
