package com.formento.web.models;

import java.time.LocalDateTime;

public record HealthResponse(String status, LocalDateTime timestamp, String message) {}