package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.entity.PushToken;
import com.janne.dreialbenapiproxy.model.AlbumDto;
import com.janne.dreialbenapiproxy.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.webhook.url:}")
    private String webhookUrl;

    public void notifyWebhook(List<AlbumDto> newAlbums) {
        if (newAlbums.isEmpty()) {
            log.debug("No new albums to notify about");
            return;
        }

        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Webhook URL not configured, skipping webhook notification for {} album(s)", newAlbums.size());
            return;
        }

        log.info("Sending webhook notification for {} new album(s) to {}", newAlbums.size(), webhookUrl);

        Map<String, Object> payload = Map.of(
            "albums", newAlbums,
            "callbackUrl", "https://drei-alben.jannekeiper.de/api/v1/notifications"
        );

        WebClient webClient = webClientBuilder.build();

        webClient.post()
            .uri(webhookUrl)
            .bodyValue(payload)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
                log.error("Webhook POST failed. Status: {}", response.statusCode());
                return response.bodyToMono(String.class)
                    .doOnNext(errorBody -> log.error("Webhook error body: {}", errorBody))
                    .then(Mono.empty());
            })
            .bodyToMono(String.class)
            .doOnNext(response -> log.info("Webhook response: {}", response))
            .doOnError(error -> log.error("Error sending webhook notification", error))
            .onErrorResume(error -> {
                log.error("Failed to send webhook notification, continuing...", error);
                return Mono.empty();
            })
            .subscribe();
    }

    public int sendNotificationToAllEnabled(String title, String body) {
        List<PushToken> enabledTokens = pushTokenRepository.findByEnabledTrue();
        if (enabledTokens.isEmpty()) {
            log.debug("No enabled push tokens found");
            return 0;
        }

        log.info("Sending notification to {} enabled devices", enabledTokens.size());

        Flux.fromIterable(enabledTokens)
            .buffer(BATCH_SIZE)
            .flatMap(tokenBatch -> sendBatchNotification(tokenBatch, title, body))
            .doOnError(error -> log.error("Error sending push notifications", error))
            .blockLast();

        return enabledTokens.size();
    }

    private Mono<Void> sendBatchNotification(List<PushToken> tokens, String title, String body) {
        WebClient webClient = webClientBuilder
                .baseUrl(EXPO_PUSH_URL)
                .build();

        List<Map<String, Object>> messages = tokens.stream()
                .map(token -> Map.<String, Object>of(
                    "to", token.getToken(),
                    "sound", "default",
                    "title", title,
                    "body", body,
                    "data", Map.of("type", "NEW_ALBUM")
                ))
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

    public int sendTestNotification(String title, String body, boolean onlyEnabled) {
        List<PushToken> tokens = onlyEnabled
                ? pushTokenRepository.findByEnabledTrue()
                : pushTokenRepository.findAll();

        if (tokens.isEmpty()) {
            log.debug("No push tokens found for test notification");
            return 0;
        }

        log.info("Sending test notification to {} devices (onlyEnabled: {})", tokens.size(), onlyEnabled);

        Flux.fromIterable(tokens)
                .buffer(BATCH_SIZE)
                .flatMap(tokenBatch -> sendTestBatchNotification(tokenBatch, title, body))
                .doOnError(error -> log.error("Error sending test notifications", error))
                .blockLast();

        return tokens.size();
    }

    private Mono<Void> sendTestBatchNotification(List<PushToken> tokens, String title, String body) {
        WebClient webClient = webClientBuilder
                .baseUrl(EXPO_PUSH_URL)
                .build();

        List<Map<String, Object>> messages = tokens.stream()
                .map(token -> buildTestPushMessage(token.getToken(), title, body))
                .toList();

        return webClient.post()
                .bodyValue(messages)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Failed to send test notifications. Status: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                            .doOnNext(errorBody -> log.error("Error body: {}", errorBody))
                            .then(Mono.empty());
                })
                .bodyToMono(String.class)
                .doOnNext(response -> log.debug("Expo push response: {}", response))
                .doOnError(error -> log.error("Error sending test batch notification", error))
                .then()
                .onErrorResume(error -> {
                    log.error("Failed to send test notification batch, continuing...", error);
                    return Mono.empty();
                });
    }

    private Map<String, Object> buildTestPushMessage(String token, String title, String body) {
        return Map.of(
                "to", token,
                "sound", "default",
                "title", title,
                "body", body,
                "data", Map.of(
                        "type", "TEST_NOTIFICATION",
                        "timestamp", System.currentTimeMillis()
                )
        );
    }
}
