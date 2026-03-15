package com.example.demo.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "account_roles",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_account_role",
                columnNames = {"account_id","role_id"}
        ),
        indexes = {
                @Index(name = "idx_account_roles_account", columnList = "account_id"),
                @Index(name = "idx_account_roles_role", columnList = "role_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRole extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
