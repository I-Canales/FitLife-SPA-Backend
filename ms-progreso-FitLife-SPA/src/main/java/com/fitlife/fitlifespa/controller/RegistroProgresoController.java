package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.RegistroProgresoRequestDTO;
import com.fitlife.fitlifespa.dto.RegistroProgresoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.RegistroProgresoService;
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
@RequestMapping("/api/progreso")
@Tag(name = "Progreso", description = "Seguimiento físico de los socios de FitLife SPA")
public class RegistroProgresoController {

    private final RegistroProgresoService service;

    public RegistroProgresoController(RegistroProgresoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los registros de progreso")
    public ResponseEntity<CollectionModel<EntityModel<RegistroProgresoResponseDTO>>> obtenerTodos() {
        List<RegistroProgresoResponseDTO> registros = service.obtenerTodos();
        List<EntityModel<RegistroProgresoResponseDTO>> modelo = registros.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(RegistroProgresoController.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de progreso por su ID")
    public ResponseEntity<EntityModel<RegistroProgresoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(ensamblar(r)))
                .orElseThrow(() -> new ResourceNotFoundException("El registro de progreso con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo registro de progreso físico")
    public ResponseEntity<EntityModel<RegistroProgresoResponseDTO>> crear(@Valid @RequestBody RegistroProgresoRequestDTO dto) {
        RegistroProgresoResponseDTO nuevo = service.guardar(dto);
        log.info("POST /api/progreso -> registro creado id={}", nuevo.getId());
        return ResponseEntity.created(linkTo(methodOn(RegistroProgresoController.class).obtenerPorId(nuevo.getId())).toUri())
                .body(ensamblar(nuevo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un registro de progreso")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.eliminar(id)) {
            throw new ResourceNotFoundException("El registro de progreso con id " + id + " no existe");
        }
        return ResponseEntity.noContent().build();
    }

    private EntityModel<RegistroProgresoResponseDTO> ensamblar(RegistroProgresoResponseDTO r) {
        return EntityModel.of(r,
                linkTo(methodOn(RegistroProgresoController.class).obtenerPorId(r.getId())).withSelfRel(),
                linkTo(methodOn(RegistroProgresoController.class).eliminar(r.getId())).withRel("eliminar"));
    }
}
