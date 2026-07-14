package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "clase_grupal")
public class ClaseGrupal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la clase no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre de la clase", example = "Zumba")
    private String nombre;

    @NotBlank(message = "El tipo de clase no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Tipo/categoría de la clase", example = "Cardio")
    private String tipo;

    @NotBlank(message = "El horario no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Horario de la clase", example = "Lunes 18:00")
    private String horario;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    @Column(nullable = false)
    @Schema(description = "Cupo máximo de socios en la clase", example = "20")
    private Integer cupoMaximo;

    @Column(name = "entrenador_id")
    @Schema(description = "ID del entrenador a cargo (ms-entrenadores)", example = "1")
    private Long entrenadorId;

    @Column(nullable = false)
    @Schema(description = "Indica si la clase sigue activa", example = "true")
    private boolean activa = true;
}
