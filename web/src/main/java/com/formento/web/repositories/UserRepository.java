package com.formento.web.repositories;

import com.formento.web.models.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
    
    @Query("SELECT * FROM users WHERE email = :email")
    Mono<UserEntity> findByEmail(String email);
    
    @Query("SELECT * FROM users WHERE id = :id")
    Mono<UserEntity> findById(Long id);
}