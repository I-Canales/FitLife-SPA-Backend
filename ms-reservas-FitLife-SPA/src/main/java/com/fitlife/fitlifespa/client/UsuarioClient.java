package com.fitlife.fitlifespa.client;

import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Valida contra ms-socios que el usuario que hace la reserva exista y esté activo,
 * antes de confirmar la reserva de una clase grupal.
 */
@Slf4j
@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Qualifier("usuariosWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean validarUsuarioActivo(Long usuarioId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/api/usuarios/{id}/existe", usuarioId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response -> Mono.empty())
                    .bodyToMono(Boolean.class)
                    .defaultIfEmpty(false)
                    .block();

            log.info("Validación remota de usuarioId={} en ms-socios -> existe/activo={}", usuarioId, existe);
            return Boolean.TRUE.equals(existe);

        } catch (WebClientResponseException e) {
            log.error("ms-socios respondió con error HTTP {} al validar usuarioId={}", e.getStatusCode(), usuarioId);
            throw new ServicioRemotoException(
                    "ms-socios respondió con un error inesperado (" + e.getStatusCode() + ") al validar el usuario " + usuarioId, e);
        } catch (WebClientRequestException e) {
            log.error("No fue posible conectar con ms-socios para validar usuarioId={}", usuarioId, e);
            throw new ServicioRemotoException(
                    "No fue posible contactar a ms-socios (¿está caído?) para validar el usuario " + usuarioId, e);
        } catch (Exception e) {
            log.error("Timeout u otro error validando usuarioId={} contra ms-socios", usuarioId, e);
            throw new ServicioRemotoException(
                    "Tiempo de espera agotado al validar el usuario " + usuarioId + " contra ms-socios", e);
        }
    }
}
