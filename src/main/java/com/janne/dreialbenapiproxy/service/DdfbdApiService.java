package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.model.incomming.ddfdb.AlbumDtoIncomming;
import com.janne.dreialbenapiproxy.model.incomming.ddfdb.AlbumResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class DdfbdApiService {

    private final WebClient ddfdbWebClient;
    private final ObjectMapper objectMapper;
    @Value("${app.external.secret}")
    private String API_SECRET;

    public Flux<AlbumDto> getAllAlbumsFromDdfdb() {
        final int limit = 20;

        return fetchAlbumsPage(limit, 0)
            .expand(response -> {
                if (response.items().size() < limit) {
                    return Mono.empty(); // stop
                }

                int nextOffset = response.offset() + limit;
                return fetchAlbumsPage(limit, nextOffset);
            })
            .flatMapIterable(AlbumResponseDto::items)
            .map(AlbumDtoIncomming::toAlbumDto);
    }

    private Mono<AlbumResponseDto> fetchAlbumsPage(int limit, int offset) {
        return ddfdbWebClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/folgen")
                .queryParam("apikey", API_SECRET)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build()
            )
            .retrieve()
            .bodyToMono(AlbumResponseDto.class);
    }

}