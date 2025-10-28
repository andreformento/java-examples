package com.formento.web.models;

import java.time.LocalDateTime;

public record User(Long id, String name, String email, LocalDateTime createdAt) {}