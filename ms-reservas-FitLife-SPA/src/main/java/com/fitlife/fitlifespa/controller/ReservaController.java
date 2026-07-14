package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.ReservaRequestDTO;
import com.fitlife.fitlifespa.dto.ReservaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.ReservaService;
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
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reservas de clases grupales de FitLife SPA. Valida al socio contra ms-socios.")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las reservas")
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponseDTO>>> obtenerTodas() {
        List<ReservaResponseDTO> reservas = service.obtenerTodos();
        List<EntityModel<ReservaResponseDTO>> modelo = reservas.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(ReservaController.class).obtenerTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por su ID")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(ensamblar(r)))
                .orElseThrow(() -> new ResourceNotFoundException("La reserva con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Valida contra ms-socios que el usuario exista y esté activo antes de confirmar la reserva.")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO nueva = service.guardar(dto);
        log.info("POST /api/reservas -> reserva creada id={}", nueva.getId());
        return ResponseEntity.created(linkTo(methodOn(ReservaController.class).obtenerPorId(nueva.getId())).toUri())
                .body(ensamblar(nueva));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.cancelar(id)));
    }

    private EntityModel<ReservaResponseDTO> ensamblar(ReservaResponseDTO r) {
        EntityModel<ReservaResponseDTO> modelo = EntityModel.of(r,
                linkTo(methodOn(ReservaController.class).obtenerPorId(r.getId())).withSelfRel());
        if ("CONFIRMADA".equals(r.getEstado())) {
            modelo.add(linkTo(methodOn(ReservaController.class).cancelar(r.getId())).withRel("cancelar"));
        }
        return modelo;
    }
}
