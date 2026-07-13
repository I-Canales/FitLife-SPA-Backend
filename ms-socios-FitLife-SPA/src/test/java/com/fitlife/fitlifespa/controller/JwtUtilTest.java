package com.fitlife.fitlifespa.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fitlife.fitlifespa.config.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    @DisplayName("generarToken() debería crear un token JWT no vacío")
    void generarTokenDeberiaCrearTokenNoVacio() {
        String token = jwtUtil.generarToken("juan.perez@example.com");
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("extraerEmail() debería devolver el mismo email usado al generar el token")
    void extraerEmailDeberiaDevolverElEmailOriginal() {
        String email = "juan.perez@example.com";
        String token = jwtUtil.generarToken(email);
        String emailExtraido = jwtUtil.extraerEmail(token);
        assertThat(emailExtraido).isEqualTo(email);
    }

    @Test
    @DisplayName("esTokenValido() debería retornar true para un token recién generado con el email correcto")
    void esTokenValidoDeberiaRetornarTrueParaTokenValido() {
        String email = "juan.perez@example.com";
        String token = jwtUtil.generarToken(email);
        boolean esValido = jwtUtil.esTokenValido(token, email);
        assertThat(esValido).isTrue();
    }

    @Test
    @DisplayName("esTokenValido() debería retornar false si el email no coincide con el del token")
    void esTokenValidoDeberiaRetornarFalseParaEmailDistinto() {
        String token = jwtUtil.generarToken("juan.perez@example.com");
        boolean esValido = jwtUtil.esTokenValido(token, "otro.usuario@example.com");
        assertThat(esValido).isFalse();
    }

    @Test
    @DisplayName("extraerEmail() debería lanzar excepción ante un token malformado")
    void extraerEmailDeberiaLanzarExcepcionConTokenInvalido() {
        String tokenInvalido = "esto.no.es.un.jwt.valido";
        assertThrows(Exception.class, () -> jwtUtil.extraerEmail(tokenInvalido));
    }
}