package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoResponseDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private String estado;
    private LocalDate fechaAdquisicion;
}
