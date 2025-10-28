package com.formento.web.config;

import com.formento.web.handlers.HealthHandler;
import com.formento.web.handlers.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    private final UserHandler userHandler;
    private final HealthHandler healthHandler;

    public RouterConfig(UserHandler userHandler, HealthHandler healthHandler) {
        this.userHandler = userHandler;
        this.healthHandler = healthHandler;
    }

    @Bean
    public RouterFunction<?> routes() {
        return RouterFunctions
                .route(GET("/api/users"), userHandler::getAllUsers)
                .andRoute(GET("/api/users/{id}"), userHandler::getUserById)
                .andRoute(POST("/api/users"), userHandler::createUser)
                .andRoute(PUT("/api/users/{id}"), userHandler::updateUser)
                .andRoute(DELETE("/api/users/{id}"), userHandler::deleteUser)
                .andRoute(GET("/api/health"), healthHandler::healthCheck);
    }
}