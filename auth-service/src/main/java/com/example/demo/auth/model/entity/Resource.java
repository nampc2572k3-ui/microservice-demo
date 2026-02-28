package com.example.demo.auth.model.entity;

import com.example.demo.common.constant.ActionType;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "resources")
public class Resource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(name = "method", nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private String pattern;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;
}
