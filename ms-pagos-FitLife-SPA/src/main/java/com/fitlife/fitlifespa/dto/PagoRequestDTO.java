package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PagoRequestDTO {
    @NotNull(message = "El id del usuario es obligatorio")
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    @Schema(example = "29990")
    private Double monto;

    @NotNull(message = "La fecha de pago es obligatoria")
    @Schema(example = "2026-07-01")
    private LocalDate fechaPago;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Schema(example = "Tarjeta de crédito")
    private String metodoPago;
}
