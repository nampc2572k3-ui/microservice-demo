package com.example.demo.domain.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AssignMenuRequest {

    @NotNull
    private Long menuId;

    @Min(1) @Max(7)
    private int bitmask;

}
