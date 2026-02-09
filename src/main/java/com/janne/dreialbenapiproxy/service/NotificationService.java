package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.entity.PushToken;
import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private static final int BATCH_SIZE = 100;

    private final PushTokenRepository pushTokenRepository;
    private final WebClient.Builder webClientBuilder;

    public void sendNewAlbumNotifications(List<AlbumDto> newAlbums) {
        if (newAlbums.isEmpty()) {
            log.debug("No new albums to notify about");
            return;
        }

        List<PushToken> enabledTokens = pushTokenRepository.findByEnabledTrue();
        if (enabledTokens.isEmpty()) {
            log.debug("No enabled push tokens found");
            return;
        }

        String notificationBody = buildNotificationBody(newAlbums);
        String notificationTitle = newAlbums.size() == 1 
            ? "Neue Folge Veröffentlicht!"
            : newAlbums.size() + " Neue Folgen Veröffentlicht!";

        log.info("Sending notifications for {} new album(s) to {} devices", newAlbums.size(), enabledTokens.size());

        Flux.fromIterable(enabledTokens)
            .buffer(BATCH_SIZE)
            .flatMap(tokenBatch -> sendBatchNotification(tokenBatch, notificationTitle, notificationBody, newAlbums))
            .doOnError(error -> log.error("Error sending push notifications", error))
            .subscribe();
    }

    private String buildNotificationBody(List<AlbumDto> newAlbums) {
        if (newAlbums.size() == 1) {
            AlbumDto album = newAlbums.getFirst();
            return album.name() + " wurde veröffentlicht";
        } else {
            return "Neue Folgen veröffentlicht";
        }
    }

    private Mono<Void> sendBatchNotification(List<PushToken> tokens, String title, String body, List<AlbumDto> albums) {
        WebClient webClient = webClientBuilder
            .baseUrl(EXPO_PUSH_URL)
            .build();

        List<Map<String, Object>> messages = tokens.stream()
            .map(token -> buildPushMessage(token.getToken(), title, body, albums))
            .toList();

        return webClient.post()
            .bodyValue(messages)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
                log.error("Failed to send push notifications. Status: {}", response.statusCode());
                return response.bodyToMono(String.class)
                    .doOnNext(errorBody -> log.error("Error body: {}", errorBody))
                    .then(Mono.empty());
            })
            .bodyToMono(String.class)
            .doOnNext(response -> log.debug("Expo push response: {}", response))
            .doOnError(error -> log.error("Error sending batch notification", error))
            .then()
            .onErrorResume(error -> {
                log.error("Failed to send notification batch, continuing...", error);
                return Mono.empty();
            });
    }

    private Map<String, Object> buildPushMessage(String token, String title, String body, List<AlbumDto> albums) {
        return Map.of(
            "to", token,
            "sound", "default",
            "title", title,
            "body", body,
            "data", Map.of(
                "type", "NEW_ALBUM",
                "albumCount", albums.size(),
                "albumId", albums.getFirst()._id()
            )
        );
    }
}
