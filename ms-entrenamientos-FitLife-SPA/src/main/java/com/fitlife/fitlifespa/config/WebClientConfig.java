package com.fitlife.fitlifespa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configura el cliente HTTP reactivo (WebClient) usado para la comunicación
 * síncrona entre microservicios: ms-entrenamientos -> ms-socios.
 * La URL base es configurable vía application.properties (ms-socios.url),
 * de modo que en producción pueda apuntar al Gateway en lugar de al puerto directo.
 */
@Configuration
public class WebClientConfig {

    @Value("${ms-socios.url:http://localhost:8081}")
    private String msSociosUrl;

    @Value("${ms-socios.timeout-ms:3000}")
    private long timeoutMs;

    @Bean
    public WebClient usuariosWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeoutMs));

        return WebClient.builder()
                .baseUrl(msSociosUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
