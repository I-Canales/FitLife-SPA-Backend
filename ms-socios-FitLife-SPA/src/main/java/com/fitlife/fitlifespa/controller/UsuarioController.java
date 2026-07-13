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

import com.fitlife.fitlifespa.dto.UsuarioRequestDTO;
import com.fitlife.fitlifespa.dto.UsuarioResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.UsuarioService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones para la gestión de usuarios y perfiles en FitLife SPA")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna la colección de usuarios registrados, cada uno con sus enlaces HATEOAS.")
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponseDTO>>> obtenerTodos() {
        List<UsuarioResponseDTO> usuarios = service.obtenerTodos();

        List<EntityModel<UsuarioResponseDTO>> usuariosModel = usuarios.stream()
                .map(this::ensamblarModelo)
                .collect(Collectors.toList());

        if (usuariosModel.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        CollectionModel<EntityModel<UsuarioResponseDTO>> collectionModel = CollectionModel.of(
                usuariosModel,
                linkTo(methodOn(UsuarioController.class).obtenerTodos()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su ID", description = "Busca un usuario en la base de datos y retorna sus datos junto con enlaces hipermedia.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "El usuario solicitado no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> getUsuarioById(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(usuario -> ResponseEntity.ok(ensamblarModelo(usuario)))
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en el sistema FitLife SPA.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "La petición contiene datos inválidos", content = @Content)
    })
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> createUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO nuevoUsuario = service.guardar(dto);
        log.info("POST /api/usuarios -> usuario creado id={}", nuevoUsuario.getId());

        return ResponseEntity
                .created(linkTo(methodOn(UsuarioController.class).getUsuarioById(nuevoUsuario.getId())).toUri())
                .body(ensamblarModelo(nuevoUsuario));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un usuario", description = "Cambia el estado del usuario a inactivo. El enlace 'desactivar' desaparece una vez aplicado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
        @ApiResponse(responseCode = "404", description = "El usuario solicitado no existe", content = @Content)
    })
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> desactivarUsuario(@PathVariable Long id) {
        UsuarioResponseDTO actualizado = service.desactivar(id);
        return ResponseEntity.ok(ensamblarModelo(actualizado));
    }

    @GetMapping("/{id}/existe")
    @Operation(summary = "Verificar existencia y estado de un usuario",
            description = "Endpoint interno usado por otros microservicios (ms-entrenamientos) a través del Gateway, para validar que un usuario existe y está activo antes de asociarle recursos.")
    public ResponseEntity<Boolean> existeYActivo(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeYActivo(id));
    }

    private EntityModel<UsuarioResponseDTO> ensamblarModelo(UsuarioResponseDTO usuario) {
        EntityModel<UsuarioResponseDTO> modelo = EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel()
        );

        if (usuario.isActivo()) {
            modelo.add(linkTo(methodOn(UsuarioController.class).desactivarUsuario(usuario.getId())).withRel("desactivar"));
        }

        return modelo;
    }
}
