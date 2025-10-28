package com.formento.web;

import com.formento.web.config.TestContainersConfig;
import com.formento.web.models.User;
import com.formento.web.models.UserCreateRequest;
import com.formento.web.models.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(TestContainersConfig.class)
class UserIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldGetAllUsers() {
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(User.class)
                .consumeWith(result -> {
                    var users = result.getResponseBody();
                    // Should have at least the 2 initial users from migration
                    assertThat(users).hasSizeGreaterThanOrEqualTo(2);
                    
                    // Find the initial users (they may have been updated by other tests)
                    var johnUser = users.stream()
                            .filter(u -> u.email().equals("john@example.com") || u.email().equals("john.updated@example.com"))
                            .findFirst();
                    var janeUser = users.stream()
                            .filter(u -> u.email().equals("jane@example.com") || u.email().equals("jane.updated@example.com"))
                            .findFirst();
                    
                    assertThat(johnUser).isPresent();
                    assertThat(janeUser).isPresent();
                });
    }

    @Test
    void shouldGetUserById() {
        webTestClient.get()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.id()).isEqualTo(1L);
                    assertThat(user.name()).isEqualTo("John Doe");
                    assertThat(user.email()).isEqualTo("john@example.com");
                });
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() {
        webTestClient.get()
                .uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateUser() {
        UserCreateRequest request = new UserCreateRequest("Alice Johnson", "alice@example.com");

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.name()).isEqualTo("Alice Johnson");
                    assertThat(user.email()).isEqualTo("alice@example.com");
                    assertThat(user.id()).isNotNull();
                });
    }

    @Test
    void shouldUpdateUser() {
        UserUpdateRequest request = new UserUpdateRequest("John Updated", "john.updated@example.com");

        webTestClient.put()
                .uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.id()).isEqualTo(1L);
                    assertThat(user.name()).isEqualTo("John Updated");
                    assertThat(user.email()).isEqualTo("john.updated@example.com");
                });
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() {
        UserUpdateRequest request = new UserUpdateRequest("Non Existent", "non@example.com");

        webTestClient.put()
                .uri("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteUser() {
        webTestClient.delete()
                .uri("/api/users/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String response = result.getResponseBody();
                    assertThat(response).contains("User with ID 2 deleted successfully");
                });
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() {
        webTestClient.delete()
                .uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateUserWithEmptyFields() {
        UserCreateRequest invalidRequest = new UserCreateRequest("", "");

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.name()).isEqualTo("");
                    assertThat(user.email()).isEqualTo("");
                });
    }

    @Test
    void shouldVerifyUserWasActuallyDeleted() {
        // Create a user first
        UserCreateRequest request = new UserCreateRequest("Delete Me", "delete@example.com");
        User createdUser = webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        Long userId = createdUser.id();

        // Verify user exists
        webTestClient.get()
                .uri("/api/users/" + userId)
                .exchange()
                .expectStatus().isOk();

        // Delete the user
        webTestClient.delete()
                .uri("/api/users/" + userId)
                .exchange()
                .expectStatus().isOk();

        // Verify user no longer exists
        webTestClient.get()
                .uri("/api/users/" + userId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldVerifyUserWasActuallyUpdated() {
        UserUpdateRequest request = new UserUpdateRequest("Jane Updated", "jane.updated@example.com");

        // Update user
        webTestClient.put()
                .uri("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        // Verify the update persisted
        webTestClient.get()
                .uri("/api/users/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.name()).isEqualTo("Jane Updated");
                    assertThat(user.email()).isEqualTo("jane.updated@example.com");
                });
    }

    @Test
    void shouldVerifyCreatedUserPersists() {
        UserCreateRequest request = new UserCreateRequest("Test User", "test@example.com");

        // Create user
        User createdUser = webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .returnResult()
                .getResponseBody();

        assertThat(createdUser).isNotNull();
        Long userId = createdUser.id();

        // Verify user persists in database
        webTestClient.get()
                .uri("/api/users/" + userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.id()).isEqualTo(userId);
                    assertThat(user.name()).isEqualTo("Test User");
                    assertThat(user.email()).isEqualTo("test@example.com");
                });
    }
}