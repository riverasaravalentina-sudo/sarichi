package com.sarichi.crocheting.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.sarichi.crocheting.dto.MensajeDTO;
import com.sarichi.crocheting.service.ChatService;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/{pedidoId}")
    @SendTo("/topic/chat/{pedidoId}")
    public MensajeDTO enviarMensaje(@DestinationVariable String pedidoId,
                                    @Payload MensajeDTO mensaje) {
        return chatService.enviarMensaje(
                pedidoId,
                mensaje.getRemitenteId(),
                mensaje.getRemitenteNombre(),
                mensaje.getRemitenteRol(),
                mensaje.getContenido());
    }

    @MessageMapping("/chat/{pedidoId}/typing")
    @SendTo("/topic/chat/{pedidoId}/typing")
    public Map<String, Object> typing(@DestinationVariable String pedidoId,
                                       @Payload Map<String, Object> payload) {
        return Map.of(
            "usuarioId", payload.get("usuarioId"),
            "usuarioNombre", payload.get("usuarioNombre"),
            "usuarioRol", payload.get("usuarioRol"),
            "typing", true
        );
    }
}
