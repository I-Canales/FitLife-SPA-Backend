package com.fitlife.fitlifespa.client;

import com.fitlife.fitlifespa.exception.ServicioRemotoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ClaseClient {

    private final WebClient webClient;

    public ClaseClient(@Qualifier("clasesWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean validarClaseExiste(Long claseId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/api/clases/{id}", claseId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response -> Mono.empty())
                    .bodyToMono(Object.class)
                    .map(o -> true)
                    .defaultIfEmpty(false)
                    .block();
            log.info("Validación remota de claseId={} en ms-clases -> existe={}", claseId, existe);
            return Boolean.TRUE.equals(existe);
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (WebClientResponseException e) {
            log.error("ms-clases respondió con error HTTP {} al validar claseId={}", e.getStatusCode(), claseId);
            throw new ServicioRemotoException("ms-clases respondió con un error inesperado al validar la clase " + claseId, e);
        } catch (WebClientRequestException e) {
            log.error("No fue posible conectar con ms-clases para validar claseId={}", claseId, e);
            throw new ServicioRemotoException("No fue posible contactar a ms-clases para validar la clase " + claseId, e);
        } catch (Exception e) {
            log.error("Timeout u otro error validando claseId={} contra ms-clases", claseId, e);
            throw new ServicioRemotoException("Tiempo de espera agotado al validar la clase " + claseId, e);
        }
    }
}
