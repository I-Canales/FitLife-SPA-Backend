package com.fitlife.fitlifespa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "entrenador")
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(example = "Diego Fuentes")
    private String nombre;

    @NotBlank(message = "La especialidad no puede estar vacía")
    @Column(nullable = false)
    @Schema(example = "Crossfit")
    private String especialidad;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(nullable = false)
    @Schema(example = "+56912345678")
    private String telefono;

    @Email(message = "El formato del email no es válido")
    @Column(nullable = false)
    @Schema(example = "diego.fuentes@fitlife.com")
    private String email;

    @Column(nullable = false)
    @Schema(example = "true")
    private boolean activo = true;
}
