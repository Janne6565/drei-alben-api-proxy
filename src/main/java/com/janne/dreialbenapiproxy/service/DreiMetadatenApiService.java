package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.model.incomming.diedreimetadaten.DreiMetaDatenAlbumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DreiMetadatenApiService {

    private final WebClient dreiMetadatenWebClient;

    public Mono<DreiMetaDatenAlbumDto> fetchAlbumFromDreiMetadaten(String albumNummer) {
        log.info("Fetching album metadata from DreiMetadaten for album {}", albumNummer);
        return dreiMetadatenWebClient.get()
            .uri("/Serie/" + albumNummer + "/metadata.json")
            .retrieve()
            .bodyToMono(DreiMetaDatenAlbumDto.class);
    }}
