package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.model.AlbumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final DdfbdApiService ddfbdApiService;
    private final DreiMetadatenApiService dreiMetadatenApiService;
    private final NotificationService notificationService;
    private List<AlbumDto> albums;
    private Set<String> previousAlbumIds = new HashSet<>();

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
            .doOnNext(newAlbums -> {
                log.info("Loaded {} albums", newAlbums.size());
                
                // Detect new albums
                if (!previousAlbumIds.isEmpty()) {
                    Set<String> currentAlbumIds = newAlbums.stream()
                        .map(AlbumDto::_id)
                        .collect(Collectors.toSet());
                    
                    List<AlbumDto> newlyAddedAlbums = newAlbums.stream()
                        .filter(album -> !previousAlbumIds.contains(album._id()))
                        .toList();
                    
                    if (!newlyAddedAlbums.isEmpty()) {
                        log.info("Detected {} new album(s)", newlyAddedAlbums.size());
                        notificationService.sendNewAlbumNotifications(newlyAddedAlbums);
                    }
                    
                    previousAlbumIds = currentAlbumIds;
                } else {
                    // First load, just store the IDs
                    previousAlbumIds = newAlbums.stream()
                        .map(AlbumDto::_id)
                        .collect(Collectors.toSet());
                    log.info("Initial album load complete, tracking {} album IDs", previousAlbumIds.size());
                }
                
                this.albums = newAlbums;
            })
            .subscribe();
    }
}
