package com.example.demo.core.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AssignRoleRequest {

    @NotNull
    private Long roleId;

}
