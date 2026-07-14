package com.fitlife.fitlifespa.controller;

import com.fitlife.fitlifespa.dto.PagoRequestDTO;
import com.fitlife.fitlifespa.dto.PagoResponseDTO;
import com.fitlife.fitlifespa.exception.ResourceNotFoundException;
import com.fitlife.fitlifespa.service.PagoService;
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
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Gestión de pagos y facturación de FitLife SPA")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los pagos")
    public ResponseEntity<CollectionModel<EntityModel<PagoResponseDTO>>> obtenerTodos() {
        List<PagoResponseDTO> pagos = service.obtenerTodos();
        List<EntityModel<PagoResponseDTO>> modelo = pagos.stream().map(this::ensamblar).collect(Collectors.toList());
        if (modelo.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(CollectionModel.of(modelo, linkTo(methodOn(PagoController.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por su ID")
    public ResponseEntity<EntityModel<PagoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(ensamblar(p)))
                .orElseThrow(() -> new ResourceNotFoundException("El pago con id " + id + " no existe"));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo pago")
    public ResponseEntity<EntityModel<PagoResponseDTO>> crear(@Valid @RequestBody PagoRequestDTO dto) {
        PagoResponseDTO nuevo = service.guardar(dto);
        log.info("POST /api/pagos -> pago registrado id={}", nuevo.getId());
        return ResponseEntity.created(linkTo(methodOn(PagoController.class).obtenerPorId(nuevo.getId())).toUri())
                .body(ensamblar(nuevo));
    }

    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular un pago")
    public ResponseEntity<EntityModel<PagoResponseDTO>> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ensamblar(service.anular(id)));
    }

    private EntityModel<PagoResponseDTO> ensamblar(PagoResponseDTO p) {
        EntityModel<PagoResponseDTO> modelo = EntityModel.of(p,
                linkTo(methodOn(PagoController.class).obtenerPorId(p.getId())).withSelfRel());
        if ("PAGADO".equals(p.getEstado())) {
            modelo.add(linkTo(methodOn(PagoController.class).anular(p.getId())).withRel("anular"));
        }
        return modelo;
    }
}
