package com.janne.dreialbenapiproxy.controller;

import com.janne.dreialbenapiproxy.model.TestNotificationRequest;
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
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final NotificationService notificationService;
    
    @Value("${app.admin.token}")
    private String adminToken;

    @PostMapping("/notifications")
    public ResponseEntity<?> sendTestNotification(
        @RequestHeader("X-Admin-Token") String providedToken,
        @Valid @RequestBody TestNotificationRequest request
    ) {
        // Verify admin token
        if (!adminToken.equals(providedToken)) {
            log.warn("Invalid admin token attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid admin token"));
        }

        try {
            int sent = notificationService.sendTestNotification(
                request.title(), 
                request.body(),
                request.shouldSendToEnabledOnly()
            );
            log.info("Test notification sent to {} devices", sent);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test notification sent",
                "deviceCount", sent,
                "onlyEnabled", request.shouldSendToEnabledOnly()
            ));
        } catch (Exception e) {
            log.error("Failed to send test notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to send notification: " + e.getMessage()));
        }
    }
}
