package com.example.demo.auth.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Builder
@Getter
@Setter
public class BaseResponse<T> {

    private int errorCode;
    private T data;
    private Object page;
    private String message;
    private boolean success;

    public <E> BaseResponse<T> withPageData(Page<E> page) {
        this.page = PageResponse.builder()
                .totalPage(page.getTotalPages())
                .totalItems((int) page.getTotalElements())
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .build();
        return this;
    }

}

