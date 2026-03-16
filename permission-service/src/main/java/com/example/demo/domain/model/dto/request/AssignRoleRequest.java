package com.example.demo.domain.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AssignRoleRequest {

    @NotNull
    private Long roleId;

}
