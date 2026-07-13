package com.fitlife.fitlifespa.client;

import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UsuarioClientTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private UsuarioClient usuarioClient;

    @BeforeEach
    void setUp() {
        usuarioClient = new UsuarioClient(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("validarUsuarioActivo() debería retornar true cuando ms-socios confirma que el usuario existe y está activo")
    void deberiaRetornarTrueCuandoUsuarioExisteYActivo() {
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        boolean resultado = usuarioClient.validarUsuarioActivo(1L);
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("validarUsuarioActivo() debería retornar false cuando ms-socios indica que el usuario no está activo")
    void deberiaRetornarFalseCuandoUsuarioNoEstaActivo() {
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));
        boolean resultado = usuarioClient.validarUsuarioActivo(2L);
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("validarUsuarioActivo() debería lanzar ServicioRemotoException si no logra conectarse a ms-socios")
    void deberiaLanzarExcepcionSiMsSociosEstaCaido() {
        WebClientRequestException fallaDeConexion = new WebClientRequestException(
                new RuntimeException("Connection refused"),
                HttpMethod.GET,
                URI.create("http://localhost:8081/api/usuarios/1/existe"),
                new HttpHeaders());
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.error(fallaDeConexion));
        assertThrows(ServicioRemotoException.class, () -> usuarioClient.validarUsuarioActivo(1L));
    }

    @Test
    @DisplayName("validarUsuarioActivo() debería lanzar ServicioRemotoException si ms-socios responde con error 500")
    void deberiaLanzarExcepcionSiMsSociosResponde500() {
        WebClientResponseException error500 = WebClientResponseException.create(
                500, "Internal Server Error", new HttpHeaders(), new byte[0], null);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.error(error500));
        assertThrows(ServicioRemotoException.class, () -> usuarioClient.validarUsuarioActivo(1L));
    }
}