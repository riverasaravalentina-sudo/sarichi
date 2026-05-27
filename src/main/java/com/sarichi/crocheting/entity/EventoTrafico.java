package com.sarichi.crocheting.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "eventosTrafico")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoTrafico {
    
    @Id
    private String id;
    
    @Indexed
    private LocalDateTime fecha;
    
    @Indexed
    private String tipo; // VISITA, CLICK, CONVERSION
    
    @Indexed
    private String fuente; // INSTAGRAM, GOOGLE, DIRECTO, WHATSAPP, OTRO
    
    private String url;
    private String usuarioId; // nullable
    private String sessionId;
    private String userAgent;
    private String ipAddress;
    
    // Para conversiones
    private String pedidoId; // nullable
    private Double valorConversion; // nullable
}
