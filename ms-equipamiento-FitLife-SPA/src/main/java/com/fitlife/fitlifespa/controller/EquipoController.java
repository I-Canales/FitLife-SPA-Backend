package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.EquipoRequestDTO;
import com.fitlife.fitlifespa.dto.EquipoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.EquipoService;
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
@RequestMapping("/api/equipamiento")
@Tag(name = "Equipamiento", description = "Gestión del inventario de equipos de FitLife SPA")
public class EquipoController {

    private final EquipoService service;

    public EquipoController(EquipoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los equipos")
    public ResponseEntity<CollectionModel<EntityModel<EquipoResponseDTO>>> obtenerTodos() {
        List<EquipoResponseDTO> equipos = service.obtenerTodos();
        List<EntityModel<EquipoResponseDTO>> modelo = equipos.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(EquipoController.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un equipo por su ID")
    public ResponseEntity<EntityModel<EquipoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(e -> ResponseEntity.ok(ensamblar(e)))
                .orElseThrow(() -> new ResourceNotFoundException("El equipo con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo equipo")
    public ResponseEntity<EntityModel<EquipoResponseDTO>> crear(@Valid @RequestBody EquipoRequestDTO dto) {
        EquipoResponseDTO nuevo = service.guardar(dto);
        log.info("POST /api/equipamiento -> equipo registrado id={}", nuevo.getId());
        return ResponseEntity.created(linkTo(methodOn(EquipoController.class).obtenerPorId(nuevo.getId())).toUri())
                .body(ensamblar(nuevo));
    }

    @PutMapping("/{id}/mantenimiento")
    @Operation(summary = "Marcar un equipo en mantenimiento")
    public ResponseEntity<EntityModel<EquipoResponseDTO>> marcarEnMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.marcarEnMantenimiento(id)));
    }

    private EntityModel<EquipoResponseDTO> ensamblar(EquipoResponseDTO e) {
        EntityModel<EquipoResponseDTO> modelo = EntityModel.of(e,
                linkTo(methodOn(EquipoController.class).obtenerPorId(e.getId())).withSelfRel());
        if ("DISPONIBLE".equals(e.getEstado())) {
            modelo.add(linkTo(methodOn(EquipoController.class).marcarEnMantenimiento(e.getId())).withRel("mantenimiento"));
        }
        return modelo;
    }
}
