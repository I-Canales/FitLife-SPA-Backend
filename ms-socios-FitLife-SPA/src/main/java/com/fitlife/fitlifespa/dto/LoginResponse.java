package com.fitlife.fitlifespa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginResponse {

    @Schema(description = "Token JWT generado tras autenticarse", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token, siempre Bearer", example = "Bearer")
    private String tipo = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }
}
