package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.ClaseGrupalRequestDTO;
import com.fitlife.fitlifespa.dto.ClaseGrupalResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.ClaseGrupalService;
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
@RequestMapping("/api/clases")
@Tag(name = "Clases Grupales", description = "Gestión de clases grupales de FitLife SPA")
public class ClaseGrupalController {

    private final ClaseGrupalService service;

    public ClaseGrupalController(ClaseGrupalService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las clases grupales")
    public ResponseEntity<CollectionModel<EntityModel<ClaseGrupalResponseDTO>>> obtenerTodas() {
        List<ClaseGrupalResponseDTO> clases = service.obtenerTodos();
        List<EntityModel<ClaseGrupalResponseDTO>> modelo = clases.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(ClaseGrupalController.class).obtenerTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una clase grupal por su ID")
    public ResponseEntity<EntityModel<ClaseGrupalResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(c -> ResponseEntity.ok(ensamblar(c)))
                .orElseThrow(() -> new ResourceNotFoundException("La clase con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva clase grupal")
    public ResponseEntity<EntityModel<ClaseGrupalResponseDTO>> crear(@Valid @RequestBody ClaseGrupalRequestDTO dto) {
        ClaseGrupalResponseDTO nueva = service.guardar(dto);
        log.info("POST /api/clases -> clase creada id={}", nueva.getId());
        return ResponseEntity.created(linkTo(methodOn(ClaseGrupalController.class).obtenerPorId(nueva.getId())).toUri())
                .body(ensamblar(nueva));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar una clase grupal")
    public ResponseEntity<EntityModel<ClaseGrupalResponseDTO>> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.desactivar(id)));
    }

    private EntityModel<ClaseGrupalResponseDTO> ensamblar(ClaseGrupalResponseDTO c) {
        EntityModel<ClaseGrupalResponseDTO> modelo = EntityModel.of(c,
                linkTo(methodOn(ClaseGrupalController.class).obtenerPorId(c.getId())).withSelfRel());
        if (c.isActiva()) {
            modelo.add(linkTo(methodOn(ClaseGrupalController.class).desactivar(c.getId())).withRel("desactivar"));
        }
        return modelo;
    }
}
