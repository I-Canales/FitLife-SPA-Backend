package com.fitlife.fitlifespa.client;

import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Encapsula la comunicación REST con ms-socios (a través de WebClient).
 * Traduce cualquier problema de red, timeout o respuesta remota a una
 * {@link ServicioRemotoException}, para que la capa de servicio no tenga
 * que conocer los detalles del protocolo HTTP/reactivo subyacente.
 */
@Slf4j
@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient usuariosWebClient) {
        this.webClient = usuariosWebClient;
    }

    /**
     * Consulta GET /api/usuarios/{id}/existe en ms-socios.
     *
     * @param usuarioId id del usuario/socio a validar
     * @return true si el usuario existe y está activo en ms-socios
     * @throws ServicioRemotoException si ms-socios no responde, responde con error
     *                                 o supera el tiempo de espera configurado.
     */
    public boolean validarUsuarioActivo(Long usuarioId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/api/usuarios/{id}/existe", usuarioId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(false))
                    .block();

            boolean resultado = Boolean.TRUE.equals(existe);
            log.info("Validación remota en ms-socios para usuarioId={} -> existeYActivo={}", usuarioId, resultado);
            return resultado;

        } catch (WebClientRequestException ex) {
            // No hubo conexión (ms-socios caído, DNS, timeout de conexión)
            log.error("No fue posible contactar a ms-socios para validar usuarioId={}", usuarioId, ex);
            throw new ServicioRemotoException(
                    "No fue posible contactar a ms-socios para validar el usuario. Verifique que el servicio esté disponible.", ex);

        } catch (WebClientResponseException ex) {
            // ms-socios respondió pero con un error distinto de 404 (5xx, 400, etc.)
            log.error("ms-socios respondió con error {} al validar usuarioId={}", ex.getStatusCode(), usuarioId, ex);
            throw new ServicioRemotoException(
                    "ms-socios respondió con un error inesperado (" + ex.getStatusCode() + ") al validar el usuario.", ex);

        } catch (Exception ex) {
            // Cualquier otro fallo no anticipado (ej. timeout de respuesta)
            log.error("Error inesperado al validar usuarioId={} contra ms-socios", usuarioId, ex);
            throw new ServicioRemotoException(
                    "Error inesperado al validar el usuario contra ms-socios. Intente nuevamente.", ex);
        }
    }
}
