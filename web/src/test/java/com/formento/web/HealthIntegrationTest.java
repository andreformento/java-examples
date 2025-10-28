package com.formento.web;

import com.formento.web.config.TestContainersConfig;
import com.formento.web.models.HealthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(TestContainersConfig.class)
class HealthIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnHealthStatus() {
        webTestClient.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HealthResponse.class)
                .consumeWith(result -> {
                    HealthResponse health = result.getResponseBody();
                    assertThat(health).isNotNull();
                    assertThat(health.status()).isEqualTo("UP");
                    assertThat(health.message()).isEqualTo("User API is running");
                    assertThat(health.timestamp()).isNotNull();
                });
    }
}