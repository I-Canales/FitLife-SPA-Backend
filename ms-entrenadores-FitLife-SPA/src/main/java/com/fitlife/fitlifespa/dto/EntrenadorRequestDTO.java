package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntrenadorRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(example = "Diego Fuentes")
    private String nombre;

    @NotBlank(message = "La especialidad no puede estar vacía")
    @Schema(example = "Crossfit")
    private String especialidad;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Schema(example = "+56912345678")
    private String telefono;

    @Email(message = "El formato del email no es válido")
    @NotBlank(message = "El email no puede estar vacío")
    @Schema(example = "diego.fuentes@fitlife.com")
    private String email;
}
