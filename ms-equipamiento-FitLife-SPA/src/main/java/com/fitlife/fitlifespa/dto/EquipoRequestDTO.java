package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipoRequestDTO {
    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    @Schema(example = "Cinta de correr")
    private String nombre;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Schema(example = "Cardio")
    private String categoria;

    @NotNull(message = "La fecha de adquisición es obligatoria")
    @Schema(example = "2025-01-15")
    private LocalDate fechaAdquisicion;
}
