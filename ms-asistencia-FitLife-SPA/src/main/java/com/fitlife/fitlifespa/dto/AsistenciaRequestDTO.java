package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AsistenciaRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(example = "2026-07-01")
    private LocalDate fecha;

    @NotBlank(message = "La hora de entrada no puede estar vacía")
    @Schema(example = "08:30")
    private String horaEntrada;
}
