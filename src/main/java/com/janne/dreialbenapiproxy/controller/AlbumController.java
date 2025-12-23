package com.janne.dreialbenapiproxy.controller;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.service.AlbumApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumApiService albumApiService;

    @GetMapping
    public ResponseEntity<Flux<AlbumDto>> getAllAlbums() {
        return ResponseEntity.ok(albumApiService.getAllAlbums());
    }
}
