package com.janne.dreialbenapiproxy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UtilConfig {

    @Bean(name = "ddfdbWebClient")
    public WebClient webClient(@Value("${app.external.ddfdbBaseUrl}") String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Bean(name = "dreiMetadatenWebClient")
    public WebClient webClient2(@Value("${app.external.dieDreiMetadatenUrl}") String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }


}