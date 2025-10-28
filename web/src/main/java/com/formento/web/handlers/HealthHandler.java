package com.formento.web.handlers;

import com.formento.web.models.HealthResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class HealthHandler {

    public Mono<ServerResponse> healthCheck(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new HealthResponse("UP", LocalDateTime.now(), "User API is running"));
    }
}