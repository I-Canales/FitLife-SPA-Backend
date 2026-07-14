package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.EntrenadorRequestDTO;
import com.fitlife.fitlifespa.dto.EntrenadorResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.EntrenadorService;
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
@RequestMapping("/api/entrenadores")
@Tag(name = "Entrenadores", description = "Gestión de entrenadores/instructores de FitLife SPA")
public class EntrenadorController {

    private final EntrenadorService service;

    public EntrenadorController(EntrenadorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los entrenadores")
    public ResponseEntity<CollectionModel<EntityModel<EntrenadorResponseDTO>>> obtenerTodos() {
        List<EntrenadorResponseDTO> entrenadores = service.obtenerTodos();
        List<EntityModel<EntrenadorResponseDTO>> modelo = entrenadores.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(EntrenadorController.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un entrenador por su ID")
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(e -> ResponseEntity.ok(ensamblar(e)))
                .orElseThrow(() -> new ResourceNotFoundException("El entrenador con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo entrenador")
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> crear(@Valid @RequestBody EntrenadorRequestDTO dto) {
        EntrenadorResponseDTO nuevo = service.guardar(dto);
        log.info("POST /api/entrenadores -> entrenador creado id={}", nuevo.getId());
        return ResponseEntity.created(linkTo(methodOn(EntrenadorController.class).obtenerPorId(nuevo.getId())).toUri())
                .body(ensamblar(nuevo));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un entrenador")
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.desactivar(id)));
    }

    private EntityModel<EntrenadorResponseDTO> ensamblar(EntrenadorResponseDTO e) {
        EntityModel<EntrenadorResponseDTO> modelo = EntityModel.of(e,
                linkTo(methodOn(EntrenadorController.class).obtenerPorId(e.getId())).withSelfRel());
        if (e.isActivo()) {
            modelo.add(linkTo(methodOn(EntrenadorController.class).desactivar(e.getId())).withRel("desactivar"));
        }
        return modelo;
    }
}
