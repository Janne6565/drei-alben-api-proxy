package com.janne.dreialbenapiproxy.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PushTokenRequest(
    @NotBlank(message = "Token is required")
    String token,
    String deviceId,
    Boolean enabled
) {
}
