package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseGrupalResponseDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String horario;
    private Integer cupoMaximo;
    private Long entrenadorId;
    private boolean activa;
}
