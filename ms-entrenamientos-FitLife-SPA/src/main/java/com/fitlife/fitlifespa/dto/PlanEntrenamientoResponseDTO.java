package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntrenamientoResponseDTO {

    @Schema(description = "Identificador único del plan de entrenamiento", example = "1")
    private Long id;

    @Schema(description = "Nombre del plan de entrenamiento", example = "Plan Crossfit")
    private String nombrePlan;

    @Schema(description = "Nombre del entrenador a cargo del plan", example = "Diego Fuentes")
    private String entrenador;

    @Schema(description = "Duración del plan medida en semanas", example = "8")
    private Integer duracionSemanas;

    @Schema(description = "Indica si el plan sigue activo", example = "true")
    private boolean activo;

    @Schema(description = "ID del usuario/socio dueño del plan", example = "1")
    private Long usuarioId;
}
