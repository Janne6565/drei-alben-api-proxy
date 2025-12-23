package com.janne.dreialbenapiproxy.controller;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumApiService;

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAllAlbums() {
        return ResponseEntity.ok(albumApiService.getAlbums());
    }
}
