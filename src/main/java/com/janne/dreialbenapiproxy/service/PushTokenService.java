package com.janne.dreialbenapiproxy.service;

import com.janne.dreialbenapiproxy.entity.PushToken;
import com.janne.dreialbenapiproxy.model.PushTokenRequest;
import com.janne.dreialbenapiproxy.model.PushTokenResponse;
import com.janne.dreialbenapiproxy.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;

    @Transactional
    public PushTokenResponse registerToken(PushTokenRequest request) {
        log.info("Registering push token: {}", maskToken(request.token()));

        PushToken pushToken = pushTokenRepository.findByToken(request.token())
            .map(existing -> {
                log.info("Token already exists, updating: {}", maskToken(request.token()));
                existing.setDeviceId(request.deviceId());
                existing.setEnabled(request.enabled() != null ? request.enabled() : true);
                return existing;
            })
            .orElseGet(() -> {
                log.info("Creating new push token: {}", maskToken(request.token()));
                return PushToken.builder()
                    .token(request.token())
                    .deviceId(request.deviceId())
                    .enabled(request.enabled() != null ? request.enabled() : true)
                    .build();
            });

        PushToken saved = pushTokenRepository.save(pushToken);
        return toResponse(saved);
    }

    @Transactional
    public void unregisterToken(String token) {
        log.info("Unregistering push token: {}", maskToken(token));
        pushTokenRepository.findByToken(token)
            .ifPresent(pushTokenRepository::delete);
    }

    @Transactional
    public PushTokenResponse updateTokenEnabled(String token, boolean enabled) {
        log.info("Updating token enabled status: {} to {}", maskToken(token), enabled);

        PushToken pushToken = pushTokenRepository.findByToken(token)
            .orElse(PushToken.builder().token(token).build());

        pushToken.setEnabled(enabled);
        PushToken saved = pushTokenRepository.save(pushToken);
        return toResponse(saved);
    }

    private PushTokenResponse toResponse(PushToken pushToken) {
        return PushTokenResponse.builder()
            .id(pushToken.getId())
            .token(pushToken.getToken())
            .deviceId(pushToken.getDeviceId())
            .enabled(pushToken.getEnabled())
            .createdAt(pushToken.getCreatedAt())
            .updatedAt(pushToken.getUpdatedAt())
            .build();
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "***";
    }
}
