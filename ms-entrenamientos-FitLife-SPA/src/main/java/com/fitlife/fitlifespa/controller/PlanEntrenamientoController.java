package com.fitlife.fitlifespa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.fitlife.fitlifespa.dto.PlanEntrenamientoRequestDTO;
import com.fitlife.fitlifespa.dto.PlanEntrenamientoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.PlanEntrenamientoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/planes-entrenamiento")
@Tag(name = "Planes de Entrenamiento", description = "Operaciones para la gestión de planes de entrenamiento en FitLife SPA")
public class PlanEntrenamientoController {

    private final PlanEntrenamientoService service;

    public PlanEntrenamientoController(PlanEntrenamientoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los planes de entrenamiento", description = "Retorna la colección de planes registrados, cada uno con sus enlaces HATEOAS.")
    public ResponseEntity<CollectionModel<EntityModel<PlanEntrenamientoResponseDTO>>> obtenerTodos() {
        List<PlanEntrenamientoResponseDTO> planes = service.obtenerTodos();

        List<EntityModel<PlanEntrenamientoResponseDTO>> planesModel = planes.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        if (planesModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<PlanEntrenamientoResponseDTO>> collectionModel = CollectionModel.of(
                planesModel,
                linkTo(methodOn(PlanEntrenamientoController.class).obtenerTodos()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plan de entrenamiento por su ID", description = "Busca un plan en la base de datos y lo retorna junto con sus enlaces hipermedia.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan de entrenamiento encontrado correctamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PlanEntrenamientoResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "El plan de entrenamiento solicitado no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<PlanEntrenamientoResponseDTO>> getPlanEntrenamientoById(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(plan -> ResponseEntity.ok(ensamblarModelo(plan)))
                .orElseThrow(() -> new ResourceNotFoundException("El plan de entrenamiento con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo plan de entrenamiento",
            description = "Registra un nuevo plan en el sistema FitLife SPA. Antes de guardar, valida vía WebClient contra ms-socios que el usuario dueño exista y esté activo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plan de entrenamiento creado exitosamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PlanEntrenamientoResponseDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos", content = @Content),
        @ApiResponse(responseCode = "503", description = "ms-socios no respondió o el usuario no existe/no está activo", content = @Content)
    })
    public ResponseEntity<EntityModel<PlanEntrenamientoResponseDTO>> createPlanEntrenamiento(@Valid @RequestBody PlanEntrenamientoRequestDTO dto) {
        PlanEntrenamientoResponseDTO nuevoPlan = service.guardar(dto);
        log.info("POST /api/planes-entrenamiento -> plan creado id={}", nuevoPlan.getId());

        return ResponseEntity
                .created(linkTo(methodOn(PlanEntrenamientoController.class).getPlanEntrenamientoById(nuevoPlan.getId())).toUri())
                .body(ensamblarModelo(nuevoPlan));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un plan de entrenamiento", description = "Cambia el estado del plan a inactivo. El enlace 'desactivar' desaparece una vez aplicado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plan desactivado correctamente"),
        @ApiResponse(responseCode = "404", description = "El plan de entrenamiento solicitado no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<PlanEntrenamientoResponseDTO>> desactivarPlan(@PathVariable Long id) {
        PlanEntrenamientoResponseDTO actualizado = service.desactivar(id);
        return ResponseEntity.ok(ensamblarModelo(actualizado));
    }

    private EntityModel<PlanEntrenamientoResponseDTO> ensamblarModelo(PlanEntrenamientoResponseDTO plan) {
        EntityModel<PlanEntrenamientoResponseDTO> modelo = EntityModel.of(plan,
                linkTo(methodOn(PlanEntrenamientoController.class).getPlanEntrenamientoById(plan.getId())).withSelfRel()
        );

        if (plan.isActivo()) {
            modelo.add(linkTo(methodOn(PlanEntrenamientoController.class).desactivarPlan(plan.getId())).withRel("desactivar"));
        }

        return modelo;
    }
}
