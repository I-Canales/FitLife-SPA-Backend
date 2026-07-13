package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para registrar un Usuario.
 * Separa el contrato público de la API (lo que el cliente envía)
 * de la entidad JPA persistida, tal como exige la pauta del examen.
 */
@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Correo electrónico único del usuario", example = "juan.perez@example.com")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Schema(description = "Número de teléfono de contacto", example = "+56912345678")
    private String telefono;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña usada para el login", example = "MiClaveSegura123")
    private String password;
}
