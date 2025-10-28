package com.formento.web.handlers;

import com.formento.web.models.User;
import com.formento.web.models.UserCreateRequest;
import com.formento.web.models.UserEntity;
import com.formento.web.models.UserUpdateRequest;
import com.formento.web.repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class UserHandler {

    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return userRepository.findAll()
                .map(UserEntity::toUser)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users));
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        
        return userRepository.findById(id)
                .map(UserEntity::toUser)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserCreateRequest.class)
                .flatMap(userRequest -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setName(userRequest.name());
                    userEntity.setEmail(userRequest.email());
                    userEntity.setCreatedAt(LocalDateTime.now());
                    
                    return userRepository.save(userEntity)
                            .map(UserEntity::toUser)
                            .flatMap(savedUser -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(savedUser));
                })
                .onErrorResume(throwable -> 
                    ServerResponse.badRequest()
                            .bodyValue("Invalid request body: " + throwable.getMessage())
                );
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        
        return request.bodyToMono(UserUpdateRequest.class)
                .flatMap(updateRequest -> 
                    userRepository.findById(id)
                            .flatMap(existingUser -> {
                                if (updateRequest.name() != null) {
                                    existingUser.setName(updateRequest.name());
                                }
                                if (updateRequest.email() != null) {
                                    existingUser.setEmail(updateRequest.email());
                                }
                                
                                return userRepository.save(existingUser)
                                        .map(UserEntity::toUser)
                                        .flatMap(updatedUser -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(updatedUser));
                            })
                            .switchIfEmpty(ServerResponse.notFound().build())
                )
                .onErrorResume(throwable -> 
                    ServerResponse.badRequest()
                            .bodyValue("Invalid request body: " + throwable.getMessage())
                );
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        
        return userRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return userRepository.deleteById(id)
                                .then(ServerResponse.ok()
                                        .bodyValue("User with ID " + id + " deleted successfully"));
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }
}