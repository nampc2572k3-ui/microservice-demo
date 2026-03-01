package com.example.demo.common.exception.handler;


import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.utils.JsonUtils;
import com.example.demo.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            @NonNull HttpServletRequest request, HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    )
            throws IOException {

        var baseResponse = ResponseUtils.error(
                ErrorCode.USER_NOT_HAVE_PERMISSION.getCode()
                , "Access denied");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.marshal(baseResponse));
    }
}