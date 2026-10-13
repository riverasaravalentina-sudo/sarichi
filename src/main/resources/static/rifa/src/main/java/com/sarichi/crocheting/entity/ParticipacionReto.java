package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "participacionesReto")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipacionReto {
    
    @Id
    private String id;
    
    @Indexed
    private String idReto;
    
    @Indexed
    private String idUsuario;
    
    private String urlFotoParticipacion;
    private String descripcion;
    
    private LocalDateTime fechaParticipacion;
    
    @Builder.Default
    private Long votos = 0L;
    
    @Indexed
    private String estado; // PENDIENTE, APROBADA, RECHAZADA, GANADORA
    
    private String nombreUsuario;
    private String fotoPerfilUsuario;
}
