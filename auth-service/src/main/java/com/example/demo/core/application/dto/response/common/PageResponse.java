package com.example.demo.core.application.dto.response.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class PageResponse {

    private Integer totalPage;
    private Integer totalItems;
    private Integer currentPage;
    private Integer pageSize;
}
