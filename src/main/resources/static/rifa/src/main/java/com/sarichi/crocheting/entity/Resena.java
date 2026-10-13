package com.sarichi.crocheting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "resenas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    private String id;

    private String pedidoId;

    private String productoId;

    private String usuarioId;

    private Integer calificacion; // 1-5

    private String comentario;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
