package com.example.demo.core.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "menus",
        uniqueConstraints = @UniqueConstraint(name = "uq_menus_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_menus_parent", columnList = "parent_id"),
                @Index(name = "idx_menus_active", columnList = "is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @ManyToMany
    @JoinTable(
            name = "menu_resources",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_menu_resource",
                    columnNames = {"menu_id","resource_id"}
            )
    )
    private Set<Resource> resources = new HashSet<>();

}
