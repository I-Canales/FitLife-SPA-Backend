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

import com.fitlife.fitlifespa.dto.MembresiaRequestDTO;
import com.fitlife.fitlifespa.dto.MembresiaResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.MembresiaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/membresias")
@Tag(name = "Membresías", description = "Operaciones para la gestión de membresías de los socios de FitLife SPA. Requiere autenticación JWT.")
public class MembresiaController {

    private final MembresiaService service;

    public MembresiaController(MembresiaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las membresías", description = "Retorna la colección de membresías registradas, cada una con sus enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de membresías obtenida correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay membresías registradas", content = @Content)
    })
    public ResponseEntity<CollectionModel<EntityModel<MembresiaResponseDTO>>> obtenerTodas() {
        List<MembresiaResponseDTO> membresias = service.obtenerTodos();

        List<EntityModel<MembresiaResponseDTO>> membresiasModel = membresias.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        if (membresiasModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<MembresiaResponseDTO>> collectionModel = CollectionModel.of(
                membresiasModel,
                linkTo(methodOn(MembresiaController.class).obtenerTodas()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una membresía por su ID", description = "Busca una membresía en la base de datos y la retorna junto con sus enlaces hipermedia.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Membresía encontrada correctamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MembresiaResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "La membresía solicitada no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<MembresiaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(membresia -> ResponseEntity.ok(ensamblarModelo(membresia)))
                .orElseThrow(() -> new ResourceNotFoundException("La membresía con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva membresía", description = "Crea una membresía asociada a un socio en el sistema FitLife SPA.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Membresía creada exitosamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MembresiaResponseDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Los datos enviados son inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "El usuario asociado no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<MembresiaResponseDTO>> crear(@Valid @RequestBody MembresiaRequestDTO dto) {
        MembresiaResponseDTO nueva = service.guardar(dto);
        log.info("POST /api/membresias -> membresía creada id={}", nueva.getId());

        return ResponseEntity
                .created(linkTo(methodOn(MembresiaController.class).obtenerPorId(nueva.getId())).toUri())
                .body(ensamblarModelo(nueva));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una membresía", description = "Elimina definitivamente una membresía del sistema por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Membresía eliminada correctamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "La membresía solicitada no existe", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminar(id);

        if (!eliminado) {
            throw new ResourceNotFoundException("La membresía con id " + id + " no existe");
        }

        return ResponseEntity.noContent().build();
    }

    private EntityModel<MembresiaResponseDTO> ensamblarModelo(MembresiaResponseDTO membresia) {
        return EntityModel.of(membresia,
                linkTo(methodOn(MembresiaController.class).obtenerPorId(membresia.getId())).withSelfRel(),
                linkTo(methodOn(MembresiaController.class).eliminar(membresia.getId())).withRel("eliminar")
        );
    }
}
