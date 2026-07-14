package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {
    private Long id;
    private Long usuarioId;
    private String mensaje;
    private String tipo;
    private boolean leida;
    private LocalDate fechaEnvio;
}
