package com.example.demo.domain.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceResponse {
    private Long id;
    private String pathPattern;
    private String httpMethod;
    private String description;
    private String action;
    private boolean active;
}
