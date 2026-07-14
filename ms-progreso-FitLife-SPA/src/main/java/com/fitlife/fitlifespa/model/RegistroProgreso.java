package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "registro_progreso")
public class RegistroProgreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    @Schema(example = "2026-07-01")
    private LocalDate fecha;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(example = "78.5")
    private Double peso;

    @Column(name = "grasa_corporal")
    @Schema(example = "18.2")
    private Double grasaCorporal;

    @Column
    @Schema(example = "Buen progreso este mes")
    private String observaciones;
}
