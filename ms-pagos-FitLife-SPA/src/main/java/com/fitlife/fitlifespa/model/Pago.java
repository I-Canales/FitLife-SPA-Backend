package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    @Column(nullable = false)
    @Schema(example = "29990")
    private Double monto;

    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false)
    @Schema(example = "2026-07-01")
    private LocalDate fechaPago;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Column(name = "metodo_pago", nullable = false)
    @Schema(example = "Tarjeta de crédito")
    private String metodoPago;

    @Column(nullable = false)
    @Schema(example = "PAGADO")
    private String estado = "PAGADO";
}
