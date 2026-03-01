package com.example.demo.core.config.security;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.utils.JsonUtils;
import com.example.demo.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        var messageKey = authException.getMessage();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        var base = ResponseUtils.error(
                ErrorCode.SC_UNAUTHORIZED.getCode(),
                messageKey
        );

        response.getWriter().write(JsonUtils.marshal(base));
    }
}
