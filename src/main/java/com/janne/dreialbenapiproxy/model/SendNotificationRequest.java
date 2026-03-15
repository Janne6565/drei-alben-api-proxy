package com.janne.dreialbenapiproxy.model;

import jakarta.validation.constraints.NotBlank;

public record SendNotificationRequest(
    @NotBlank(message = "Title is required")
    String title,
    @NotBlank(message = "Body is required")
    String body
) {
}
