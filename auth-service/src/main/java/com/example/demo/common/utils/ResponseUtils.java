package com.example.demo.common.utils;

import com.example.demo.auth.model.dto.response.BaseResponse;
import org.springframework.data.domain.Page;

public class ResponseUtils {

    private static final int SUCCESS_CODE = 0;

    public static <T> BaseResponse<T> error(int errorCode, String message) {
        return build(false, null, message, errorCode);
    }

    public static <T> BaseResponse<T> success(String message) {
        return build(true, null, message, SUCCESS_CODE);
    }

    public static <T> BaseResponse<T> success(T data) {
        return build(true, data, null, SUCCESS_CODE);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return build(true, data, message, SUCCESS_CODE);
    }

    public static <T, E> BaseResponse<T> success(T data, String message, Page<E> page) {
        return build(true, data, message, SUCCESS_CODE)
                .withPageData(page);
    }

    private static <T> BaseResponse<T> build(
            boolean success,
            T data,
            String message,
            int errorCode
    ) {
        return BaseResponse.<T>builder()
                .success(success)
                .data(data)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
