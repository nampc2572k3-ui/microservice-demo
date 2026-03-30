package com.example.demo.core.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(
        name = "login_attempts",
        indexes = {
                @Index(name = "idx_email_time", columnList = "email, attempted_at"),
                @Index(name = "idx_ip_time", columnList = "ip_address, attempted_at")
        }
)
public class LoginAttempt extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String ipAddress;

    private boolean successful;

    private String failureReason;

    private LocalDateTime attemptedAt;
}
