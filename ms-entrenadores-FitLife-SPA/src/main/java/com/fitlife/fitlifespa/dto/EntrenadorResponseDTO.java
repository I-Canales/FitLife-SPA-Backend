package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorResponseDTO {
    private Long id;
    private String nombre;
    private String especialidad;
    private String telefono;
    private String email;
    private boolean activo;
}
