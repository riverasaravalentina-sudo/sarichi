package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Estado de un número del tablero de la rifa.
 *
 * Un documento por número (00..99). Solo se crea cuando el número
 * cambia de estado; mientras no exista, se considera DISPONIBLE.
 */
@Document(collection = "rifaNumeros")
@CompoundIndexes({
    @CompoundIndex(name = "rifa_numero_unico", def = "{'rifaId': 1, 'numero': 1}", unique = true)
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RifaNumero {

    @Id
    private String id;

    @Indexed
    private String rifaId;

    private Integer numero;

    @Indexed
    private EstadoNumero estado;

    private String compradorNombre;
    private String compradorTelefono;

    @Indexed
    private String codigoReserva;

    private LocalDateTime reservadoHasta;

    private String notas;

    private String confirmadoPor;

    private LocalDateTime actualizadoEn;
}
