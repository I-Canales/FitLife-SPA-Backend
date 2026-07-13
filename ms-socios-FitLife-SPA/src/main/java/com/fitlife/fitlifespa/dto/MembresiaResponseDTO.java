package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaResponseDTO {

    @Schema(description = "Identificador único de la membresía", example = "1")
    private Long id;

    @Schema(description = "Tipo de membresía contratada", example = "Premium")
    private String tipoPlan;

    @Schema(description = "Fecha de inicio de la membresía", example = "2026-07-01")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término de la membresía", example = "2027-07-01")
    private LocalDate fechaFin;

    @Schema(description = "Precio mensual en pesos chilenos", example = "29990")
    private Double precio;

    @Schema(description = "ID del usuario/socio dueño de la membresía", example = "1")
    private Long usuarioId;
}
