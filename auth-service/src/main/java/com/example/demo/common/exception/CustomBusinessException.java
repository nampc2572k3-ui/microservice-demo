package com.example.demo.common.exception;

import com.example.demo.common.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomBusinessException extends RuntimeException {

    private int errorCode;
    private String message;
    private Exception e;

    public CustomBusinessException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public CustomBusinessException(ErrorCode error) {
        this(error.getCode(), error.getMessage());
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return e;
    }

}

