package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.NotificacionRequestDTO;
import com.fitlife.fitlifespa.dto.NotificacionResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Gestión de notificaciones a socios de FitLife SPA")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> obtenerTodas() {
        List<NotificacionResponseDTO> notificaciones = service.obtenerTodos();
        List<EntityModel<NotificacionResponseDTO>> modelo = notificaciones.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(NotificacionController.class).obtenerTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una notificación por su ID")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(n -> ResponseEntity.ok(ensamblar(n)))
                .orElseThrow(() -> new ResourceNotFoundException("La notificación con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva notificación")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        NotificacionResponseDTO nueva = service.guardar(dto);
        log.info("POST /api/notificaciones -> notificación creada id={}", nueva.getId());
        return ResponseEntity.created(linkTo(methodOn(NotificacionController.class).obtenerPorId(nueva.getId())).toUri())
                .body(ensamblar(nueva));
    }

    @PutMapping("/{id}/leida")
    @Operation(summary = "Marcar una notificación como leída")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.marcarComoLeida(id)));
    }

    private EntityModel<NotificacionResponseDTO> ensamblar(NotificacionResponseDTO n) {
        EntityModel<NotificacionResponseDTO> modelo = EntityModel.of(n,
                linkTo(methodOn(NotificacionController.class).obtenerPorId(n.getId())).withSelfRel());
        if (!n.isLeida()) {
            modelo.add(linkTo(methodOn(NotificacionController.class).marcarComoLeida(n.getId())).withRel("marcar-leida"));
        }
        return modelo;
    }
}
