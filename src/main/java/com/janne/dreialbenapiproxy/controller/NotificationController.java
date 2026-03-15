package com.janne.dreialbenapiproxy.controller;

import com.janne.dreialbenapiproxy.model.SendNotificationRequest;
import com.janne.dreialbenapiproxy.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Value("${app.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<?> sendNotification(
        @RequestHeader("X-Webhook-Secret") String providedSecret,
        @Valid @RequestBody SendNotificationRequest request
    ) {
        if (!webhookSecret.equals(providedSecret)) {
            log.warn("Invalid webhook secret attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid webhook secret"));
        }

        try {
            int deviceCount = notificationService.sendNotificationToAllEnabled(request.title(), request.body());
            log.info("Notification sent to {} devices", deviceCount);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Notification sent",
                "deviceCount", deviceCount
            ));
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to send notification: " + e.getMessage()));
        }
    }
}
