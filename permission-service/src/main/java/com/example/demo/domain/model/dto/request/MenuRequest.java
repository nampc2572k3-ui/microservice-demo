package com.example.demo.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Set;

@Getter
public class MenuRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private Integer sortOrder;

    private Long parentId;

    private boolean active;

    private Set<Long> resourceIds;

}
