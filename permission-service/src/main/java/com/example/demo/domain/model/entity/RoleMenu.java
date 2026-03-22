package com.example.demo.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_menus",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_role_menu",
                columnNames = {"role_id","menu_id"}
        ),
        indexes = {
                @Index(name = "idx_role_menu_role", columnList = "role_id"),
                @Index(name = "idx_role_menu_menu", columnList = "menu_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private int bitmask;

}
