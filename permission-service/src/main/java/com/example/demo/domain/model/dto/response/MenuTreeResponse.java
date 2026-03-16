package com.example.demo.domain.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MenuTreeResponse {

    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;
    private Long parentId;

    private Integer bitmask;

    private List<MenuTreeResponse> children;



}
