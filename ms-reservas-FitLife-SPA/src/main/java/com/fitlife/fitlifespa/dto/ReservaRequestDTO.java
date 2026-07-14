package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "El id de la clase es obligatorio")
    @Schema(example = "1")
    private Long claseId;

    @NotNull(message = "La fecha de la reserva es obligatoria")
    @Schema(example = "2026-07-05")
    private LocalDate fechaReserva;
}
