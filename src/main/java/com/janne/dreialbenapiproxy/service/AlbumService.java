package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final DdfbdApiService ddfbdApiService;
    private final DreiMetadatenApiService dreiMetadatenApiService;
    private List<AlbumDto> albums;

    public List<AlbumDto> getAlbums() {
        if (albums == null) {
            loadAlbums();
        }
        return albums;
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void loadAlbums() {
        ddfbdApiService.getAllAlbumsFromDdfdb()
            .flatMap(albumDto -> {
                if (albumDto.number() == null || albumDto.number().isBlank()) {
                    return Mono.just(albumDto);
                }

                return dreiMetadatenApiService
                    .fetchAlbumFromDreiMetadaten(albumDto.number())
                    .map(meta -> {
                        AlbumDto.AlbumDtoBuilder builder = albumDto.toBuilder()
                            .appleMusicId(meta.ids().appleMusic());

                        if (meta.beschreibung() != null && !meta.beschreibung().isBlank()) {
                            builder.description(meta.beschreibung());
                        }

                        return builder.build();
                    });
            })
            .collectList()
            .doOnNext(albums -> {
                log.info("Loaded {} albums", albums.size());
                this.albums = albums;
            })
            .subscribe();
    }
}
