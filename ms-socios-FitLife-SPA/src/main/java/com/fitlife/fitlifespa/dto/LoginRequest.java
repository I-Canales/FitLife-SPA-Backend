package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(description = "Correo del usuario registrado", example = "juan.perez@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "MiClaveSegura123")
    private String password;
}
