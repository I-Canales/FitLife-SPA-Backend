package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NotificacionRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(example = "1")
    private Long usuarioId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Schema(example = "Tu membresía vence en 3 días")
    private String mensaje;

    @NotBlank(message = "El tipo de notificación no puede estar vacío")
    @Schema(example = "RECORDATORIO")
    private String tipo;

    @NotNull(message = "La fecha de envío es obligatoria")
    @Schema(example = "2026-07-01")
    private LocalDate fechaEnvio;
}
