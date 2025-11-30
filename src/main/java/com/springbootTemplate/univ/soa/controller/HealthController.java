package com.springbootTemplate.univ.soa.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Health health = (Health) healthEndpoint.health();
        Status status = health.getStatus();
        Map<String, Object> body = Map.of(
                "status", status.getCode(),
                "details", health.getDetails()
        );
        if (Status.UP.equals(status)) {
            return ResponseEntity.ok(body);
        } else {
            return ResponseEntity.status(503).body(body);
        }
    }
}

