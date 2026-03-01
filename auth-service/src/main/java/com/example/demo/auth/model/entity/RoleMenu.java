package com.example.demo.auth.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "role_menu",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"role_id", "menu_id", "permission"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(nullable = false)
    private Integer permission;

}
