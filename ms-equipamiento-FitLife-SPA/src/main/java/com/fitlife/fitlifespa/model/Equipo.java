package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    @Column(nullable = false)
    @Schema(example = "Cinta de correr")
    private String nombre;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Column(nullable = false)
    @Schema(example = "Cardio")
    private String categoria;

    @Column(nullable = false)
    @Schema(example = "DISPONIBLE")
    private String estado = "DISPONIBLE";

    @NotNull(message = "La fecha de adquisición es obligatoria")
    @Column(name = "fecha_adquisicion", nullable = false)
    @Schema(example = "2025-01-15")
    private LocalDate fechaAdquisicion;
}
