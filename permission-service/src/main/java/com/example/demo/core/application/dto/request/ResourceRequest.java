package com.example.demo.core.application.dto.request;

import com.example.demo.core.domain.model.enums.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ResourceRequest {
    @NotBlank
    private String pathPattern;

    @NotBlank
    @Pattern(regexp = "GET|POST|PUT|DELETE|PATCH")
    private String httpMethod;

    private String description;

    @NotNull
    private ActionType action;
}
