package com.example.demo.domain.model.entity;

import com.example.demo.domain.model.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "resources",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_resource_api",
                columnNames = {"http_method","path_pattern"}
        ),
        indexes = {
                @Index(name = "idx_resource_active", columnList = "is_active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_pattern", nullable = false)
    private String pathPattern;

    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    private String description;

    @Column(name = "is_active")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;

    @ManyToMany(mappedBy = "resources")
    private Set<Menu> menus = new HashSet<>();

}