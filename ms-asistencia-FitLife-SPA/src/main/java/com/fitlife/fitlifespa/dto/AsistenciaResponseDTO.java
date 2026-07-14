package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResponseDTO {
    private Long id;
    private Long usuarioId;
    private LocalDate fecha;
    private String horaEntrada;
    private String horaSalida;
}
