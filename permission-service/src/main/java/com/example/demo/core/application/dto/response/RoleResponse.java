package com.example.demo.core.application.dto.response;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private boolean system;

    public static List<RoleResponse> fromObject(Object value) {
        if (value instanceof List<?> list) {
            ObjectMapper mapper = new ObjectMapper();
            return list.stream()
                    .map(item -> mapper.convertValue(item, RoleResponse.class))
                    .toList();
        }

        throw new CustomBusinessException(
                ErrorCode.CACHE_DESERIALIZATION_ERROR.getCode(),
                ErrorCode.CACHE_DESERIALIZATION_ERROR.getMessage()
        );
    }
}
