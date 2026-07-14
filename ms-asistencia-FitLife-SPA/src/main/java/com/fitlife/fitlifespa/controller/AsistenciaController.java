package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.AsistenciaRequestDTO;
import com.fitlife.fitlifespa.dto.AsistenciaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.AsistenciaService;
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
@RequestMapping("/api/asistencia")
@Tag(name = "Asistencia", description = "Control de asistencia de socios a FitLife SPA")
public class AsistenciaController {

    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los registros de asistencia")
    public ResponseEntity<CollectionModel<EntityModel<AsistenciaResponseDTO>>> obtenerTodos() {
        List<AsistenciaResponseDTO> registros = service.obtenerTodos();
        List<EntityModel<AsistenciaResponseDTO>> modelo = registros.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(AsistenciaController.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de asistencia por su ID")
    public ResponseEntity<EntityModel<AsistenciaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(a -> ResponseEntity.ok(ensamblar(a)))
                .orElseThrow(() -> new ResourceNotFoundException("El registro de asistencia con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar entrada de un socio")
    public ResponseEntity<EntityModel<AsistenciaResponseDTO>> registrarEntrada(@Valid @RequestBody AsistenciaRequestDTO dto) {
        AsistenciaResponseDTO nueva = service.registrarEntrada(dto);
        log.info("POST /api/asistencia -> entrada registrada id={}", nueva.getId());
        return ResponseEntity.created(linkTo(methodOn(AsistenciaController.class).obtenerPorId(nueva.getId())).toUri())
                .body(ensamblar(nueva));
    }

    @PutMapping("/{id}/salida")
    @Operation(summary = "Registrar salida de un socio")
    public ResponseEntity<EntityModel<AsistenciaResponseDTO>> registrarSalida(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.registrarSalida(id)));
    }

    private EntityModel<AsistenciaResponseDTO> ensamblar(AsistenciaResponseDTO a) {
        EntityModel<AsistenciaResponseDTO> modelo = EntityModel.of(a,
                linkTo(methodOn(AsistenciaController.class).obtenerPorId(a.getId())).withSelfRel());
        if (a.getHoraSalida() == null) {
            modelo.add(linkTo(methodOn(AsistenciaController.class).registrarSalida(a.getId())).withRel("registrar-salida"));
        }
        return modelo;
    }
}
