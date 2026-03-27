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
        name = "oauth_providers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_provider_uid", columnNames = {"provider", "provider_uid"})
        }
)
public class OAuthProvider extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(name = "provider_uid")
    private String providerUid;

    @Lob
    private String accessToken;

    private LocalDateTime tokenExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
