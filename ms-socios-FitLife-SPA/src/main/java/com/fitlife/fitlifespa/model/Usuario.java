package com.fitlife.fitlifespa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico único del usuario, usado también para iniciar sesión", example = "juan.perez@example.com")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Schema(description = "Número de teléfono de contacto del usuario", example = "+56912345678")
    private String telefono;

    @Column(nullable = false)
    @Schema(description = "Indica si el usuario está activo dentro del sistema", example = "true")
    private boolean activo = true;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Contraseña del usuario, utilizada para el login. Nunca se retorna en las respuestas", example = "MiClaveSegura123")
    private String password;
}
