package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    @Schema(example = "1")
    private Long usuarioId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Column(nullable = false)
    @Schema(example = "Tu membresía vence en 3 días")
    private String mensaje;

    @NotBlank(message = "El tipo de notificación no puede estar vacío")
    @Column(nullable = false)
    @Schema(example = "RECORDATORIO")
    private String tipo;

    @Column(nullable = false)
    @Schema(example = "false")
    private boolean leida = false;

    @NotNull(message = "La fecha de envío es obligatoria")
    @Column(name = "fecha_envio", nullable = false)
    @Schema(example = "2026-07-01")
    private LocalDate fechaEnvio;
}
