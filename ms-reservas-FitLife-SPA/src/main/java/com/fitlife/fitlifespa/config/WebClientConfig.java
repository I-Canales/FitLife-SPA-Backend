package com.fitlife.fitlifespa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${ms-socios.url:http://localhost:8081}")
    private String msSociosUrl;

    @Value("${ms-clases.url:http://localhost:8083}")
    private String msClasesUrl;

    @Value("${ms-socios.timeout-ms:3000}")
    private long timeoutMs;

    @Bean
    public WebClient usuariosWebClient() {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(timeoutMs));
        return WebClient.builder()
                .baseUrl(msSociosUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient clasesWebClient() {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(timeoutMs));
        return WebClient.builder()
                .baseUrl(msClasesUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}