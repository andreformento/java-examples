package com.formento.web.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
public class UserEntity {
    
    @Id
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("email")
    private String email;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    public UserEntity() {}
    
    public UserEntity(Long id, String name, String email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper method to convert to DTO
    public User toUser() {
        return new User(this.id, this.name, this.email, this.createdAt);
    }
    
    public static UserEntity fromUser(User user) {
        return new UserEntity(user.id(), user.name(), user.email(), user.createdAt());
    }
}