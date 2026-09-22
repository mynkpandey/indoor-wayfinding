package com.movieinsync.wayfinding.model;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(
            String status,
            String message) {

        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}