package com.sarichi.crocheting.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/** Petición pública para apartar números (POST /api/rifa/reservar). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRifaRequestDTO {

    @NotEmpty(message = "Debes enviar al menos un número")
    private List<Integer> numeros;
}
