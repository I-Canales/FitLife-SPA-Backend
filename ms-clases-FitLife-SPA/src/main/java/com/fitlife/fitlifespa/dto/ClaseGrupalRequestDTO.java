package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaseGrupalRequestDTO {
    @NotBlank(message = "El nombre de la clase no puede estar vacío")
    @Schema(example = "Zumba")
    private String nombre;

    @NotBlank(message = "El tipo de clase no puede estar vacío")
    @Schema(example = "Cardio")
    private String tipo;

    @NotBlank(message = "El horario no puede estar vacío")
    @Schema(example = "Lunes 18:00")
    private String horario;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    @Schema(example = "20")
    private Integer cupoMaximo;

    @Schema(example = "1")
    private Long entrenadorId;
}
