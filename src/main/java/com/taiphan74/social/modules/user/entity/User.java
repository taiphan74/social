package com.taiphan74.social.modules.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username", unique = true),
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User entity")
public class User {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Column(name = "password", nullable = false)
    @Schema(description = "Encrypted password", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Column(name = "is_enabled", nullable = false)
    @Schema(description = "Account enabled status")
    private boolean enabled;

    @Column(name = "is_account_non_locked", nullable = false)
    @Schema(description = "Account non-locked status")
    private boolean accountNonLocked;

    @Column(name = "is_verified", nullable = false)
    @Schema(description = "Email verified status")
    private boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Schema(description = "User role", example = "USER")
    private ERole role;

    @Column(name = "last_login_at")
    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
