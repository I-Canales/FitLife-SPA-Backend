package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "plan_entrenamiento")
@Data
public class PlanEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del plan de entrenamiento", example = "1")
    private Long id;

    @NotBlank(message = "El nombre del plan no puede estar vacío")
    @Column(name = "nombre_plan", nullable = false)
    @Schema(description = "Nombre del plan de entrenamiento", example = "Plan Crossfit")
    private String nombrePlan;

    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    @Schema(description = "Nombre del entrenador a cargo del plan", example = "Diego Fuentes")
    private String entrenador;

    @Min(value = 1, message = "La duración debe ser de al menos 1 semana")
    @Column(name = "duracion_semanas", nullable = false)
    @Schema(description = "Duración del plan medida en semanas", example = "8")
    private Integer duracionSemanas;

    @Column(nullable = false)
    @Schema(description = "Indica si el plan de entrenamiento sigue activo", example = "true")
    private boolean activo = true;

    @NotNull(message = "El id del usuario/socio dueño del plan es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario/socio (ms-socios) al que pertenece este plan de entrenamiento", example = "1")
    private Long usuarioId;
}
