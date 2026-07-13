package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlanEntrenamientoRequestDTO {

    @NotBlank(message = "El nombre del plan no puede estar vacío")
    @Schema(description = "Nombre del plan de entrenamiento", example = "Plan Crossfit")
    private String nombrePlan;

    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    @Schema(description = "Nombre del entrenador a cargo del plan", example = "Diego Fuentes")
    private String entrenador;

    @Min(value = 1, message = "La duración debe ser de al menos 1 semana")
    @Schema(description = "Duración del plan medida en semanas", example = "8")
    private Integer duracionSemanas;

    @NotNull(message = "El id del usuario/socio dueño del plan es obligatorio")
    @Schema(description = "ID del usuario/socio (ms-socios) al que pertenece este plan", example = "1")
    private Long usuarioId;
}
