package com.sarichi.crocheting.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sarichi.crocheting.dto.MensajeDTO;
import com.sarichi.crocheting.service.ChatService;

@RestController
@RequestMapping("/mensajes")
public class MensajeController {

    private final ChatService chatService;

    public MensajeController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<MensajeDTO>> obtenerMensajes(@PathVariable String pedidoId) {
        return ResponseEntity.ok(chatService.obtenerMensajes(pedidoId));
    }

    @PostMapping("/pedido/{pedidoId}/leidos")
    public ResponseEntity<Void> marcarComoLeidos(@PathVariable String pedidoId,
                                                  @RequestBody Map<String, String> body) {
        chatService.marcarComoLeidos(pedidoId, body.get("usuarioId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pedido/{pedidoId}/no-leidos/{usuarioId}")
    public ResponseEntity<Map<String, Long>> contarNoLeidos(@PathVariable String pedidoId,
                                                             @PathVariable String usuarioId) {
        long count = chatService.contarNoLeidos(pedidoId, usuarioId);
        return ResponseEntity.ok(Map.of("noLeidos", count));
    }
}
