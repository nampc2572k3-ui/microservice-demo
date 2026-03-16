package com.example.demo.common.exception.handler;

import com.example.demo.domain.model.dto.response.BaseResponse;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        if (e instanceof BindException bindException) {
            var message = bindException.getBindingResult().getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            var response = ResponseUtils.error(
                    HttpStatus.BAD_REQUEST.value(), message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (e instanceof HandlerMethodValidationException validationException) {
            var message = validationException.getValueResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream())
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            var response = ResponseUtils.error(
                    HttpStatus.BAD_REQUEST.value(), message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        var response = ResponseUtils.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<?> handleCustomBusinessException(CustomBusinessException e) {
        var response = ResponseUtils.error(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode()).body(response);
    }


}
