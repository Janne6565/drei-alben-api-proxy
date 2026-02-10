package com.janne.dreialbenapiproxy.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TestNotificationRequest(
    @NotBlank(message = "Title is required")
    String title,
    @NotBlank(message = "Body is required")
    String body,
    Boolean onlyEnabled
) {
    public boolean shouldSendToEnabledOnly() {
        return onlyEnabled != null && onlyEnabled;
    }
}
