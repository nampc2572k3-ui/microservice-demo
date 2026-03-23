package com.example.demo.domain.model.entity;

import com.example.demo.domain.model.enums.Platform;
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
        name = "account_devices",
        indexes = {
                @Index(name = "idx_account_id", columnList = "account_id")
        }
)
public class AccountDevice extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String deviceId;

    private String deviceName;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String pushToken;

    private boolean trusted = false;

    private LocalDateTime lastActiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_id")
    private Account account;

}
