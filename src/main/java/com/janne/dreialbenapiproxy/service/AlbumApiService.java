package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.model.AlbumResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;
import tools.jackson.databind.ObjectMapper;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
@RequiredArgsConstructor
public class AlbumApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Value("${app.external.apiBaseUrl}")
    private String API_BASE_URL;
    @Value("${app.external.secret}")
    private String API_SECRET;

    public Flux<AlbumDto> getAllAlbums() {
        return webClient.get()
            .uri("/api/folgen?apikey=" + API_SECRET)
            .exchangeToMono(response ->
                response.bodyToMono(String.class)
                    .doOnNext(body -> {
                        log.info("Status code: {}", response.statusCode());
                        log.debug("Response body: {}", objectMapper.writeValueAsString(body));
                    })
                    .map(body -> Tuples.of(response.statusCode(), body))
            )
            .flatMapMany(tuple -> {
                String body = tuple.getT2();

                try {
                    AlbumResponseDto dto =
                        objectMapper.readValue(body, AlbumResponseDto.class);
                    return Flux.fromIterable(dto.items());
                } catch (Exception e) {
                    return Flux.error(e);
                }
            });
    }
}
