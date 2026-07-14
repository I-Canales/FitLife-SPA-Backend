package com.fitlife.fitlifespa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long usuarioId;
    private Double monto;
    private LocalDate fechaPago;
    private String metodoPago;
    private String estado;
}
