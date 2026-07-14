package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    @Schema(example = "2026-07-01")
    private LocalDate fecha;

    @NotBlank(message = "La hora de entrada no puede estar vacía")
    @Column(name = "hora_entrada", nullable = false)
    @Schema(example = "08:30")
    private String horaEntrada;

    @Column(name = "hora_salida")
    @Schema(example = "10:00")
    private String horaSalida;
}
