package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada para registrar una Membresía asociada a un socio.
 */
@Data
public class MembresiaRequestDTO {

    @NotBlank(message = "El tipo de plan no puede estar vacío")
    @Schema(description = "Tipo de membresía contratada", example = "Premium")
    private String tipoPlan;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha en que comienza la membresía", example = "2026-07-01")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha en que termina la membresía", example = "2027-07-01")
    private LocalDate fechaFin;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio mensual de la membresía en pesos chilenos", example = "29990")
    private Double precio;

    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(description = "ID del usuario/socio dueño de esta membresía", example = "1")
    private Long usuarioId;
}
