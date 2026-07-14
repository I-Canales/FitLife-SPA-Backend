package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroProgresoRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(example = "2026-07-01")
    private LocalDate fecha;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    @Schema(example = "78.5")
    private Double peso;

    @Schema(example = "18.2")
    private Double grasaCorporal;

    @Schema(example = "Buen progreso este mes")
    private String observaciones;
}
