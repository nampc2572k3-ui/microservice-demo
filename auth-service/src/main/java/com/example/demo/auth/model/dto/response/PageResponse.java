package com.example.demo.auth.model.dto.response;

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
