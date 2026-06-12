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

import com.sarichi.crocheting.dto.PersonalizacionDTO;
import com.sarichi.crocheting.service.PersonalizadorService;

@RestController
@RequestMapping("/personalizador")
public class PersonalizadorController {

    private final PersonalizadorService personalizadorService;

    public PersonalizadorController(PersonalizadorService personalizadorService) {
        this.personalizadorService = personalizadorService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<PersonalizacionDTO> guardar(@RequestBody Map<String, Object> body) {
        PersonalizacionDTO dto = PersonalizacionDTO.builder()
                .productoId((String) body.get("productoId"))
                .coloresSeleccionados((Map<String, String>) body.get("coloresSeleccionados"))
                .talla((String) body.get("talla"))
                .mensajeBordado((String) body.get("mensajeBordado"))
                .build();
        String usuarioId = (String) body.get("usuarioId");
        return ResponseEntity.ok(personalizadorService.guardar(dto, usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PersonalizacionDTO>> listar(@PathVariable String usuarioId) {
        return ResponseEntity.ok(personalizadorService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/compartir/{id}")
    public ResponseEntity<PersonalizacionDTO> compartir(@PathVariable String id) {
        try {
            return ResponseEntity.ok(personalizadorService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
