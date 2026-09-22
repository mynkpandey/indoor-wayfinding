package com.movieinsync.wayfinding.controller;

import com.movieinsync.wayfinding.service.GraphRecoveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final GraphRecoveryService graphRecoveryService;

    public HealthController(GraphRecoveryService graphRecoveryService) {
        this.graphRecoveryService = graphRecoveryService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "Indoor Wayfinding Backend"
        );
    }

    @GetMapping("/health/recovery")
    public Map<String, Object> recoveryHealth() {

        boolean backupAvailable =
                graphRecoveryService.isBackupAvailable();

        return Map.of(
                "status", backupAvailable ? "UP" : "DEGRADED",
                "backupAvailable", backupAvailable,
                "message", backupAvailable
                        ? "Campus graph backup is available."
                        : "Campus graph backup is unavailable."
        );
    }
}