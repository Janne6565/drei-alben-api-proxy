package com.janne.dreialbenapiproxy.controller;

import com.janne.dreialbenapiproxy.model.PushTokenRequest;
import com.janne.dreialbenapiproxy.model.PushTokenResponse;
import com.janne.dreialbenapiproxy.service.PushTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/push-tokens")
@RequiredArgsConstructor
public class PushTokenController {

    private final PushTokenService pushTokenService;

    @PostMapping("/register")
    public ResponseEntity<PushTokenResponse> registerToken(@Valid @RequestBody PushTokenRequest request) {
        PushTokenResponse response = pushTokenService.registerToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregisterToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        pushTokenService.unregisterToken(token);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{token}/enabled")
    public ResponseEntity<PushTokenResponse> updateTokenEnabled(
        @PathVariable String token,
        @RequestBody Map<String, Boolean> request
    ) {
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }
        PushTokenResponse response = pushTokenService.updateTokenEnabled(token, enabled);
        return ResponseEntity.ok(response);
    }
}
