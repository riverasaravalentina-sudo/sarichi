package com.sarichi.crocheting.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/** Petición del panel para cambiar estado de números (POST /api/rifa/numeros/estado). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRifaDTO {

    @NotEmpty(message = "Debes enviar al menos un número")
    private List<Integer> numeros;

    /** DISPONIBLE, RESERVADO o VENDIDO. */
    private String estado;

    private String compradorNombre;
    private String compradorTelefono;
    private String notas;
}
