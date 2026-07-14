package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "El id de la clase es obligatorio")
    @Column(name = "clase_id", nullable = false)
    @Schema(example = "1")
    private Long claseId;

    @NotNull(message = "La fecha de la reserva es obligatoria")
    @Column(name = "fecha_reserva", nullable = false)
    @Schema(example = "2026-07-05")
    private LocalDate fechaReserva;

    @Column(nullable = false)
    @Schema(example = "CONFIRMADA")
    private String estado = "CONFIRMADA";
}
