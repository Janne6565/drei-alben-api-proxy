package com.janne.dreialbenapiproxy.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PushTokenResponse(
    Long id,
    String token,
    String deviceId,
    Boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
