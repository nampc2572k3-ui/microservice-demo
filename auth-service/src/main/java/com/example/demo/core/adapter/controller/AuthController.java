package com.example.demo.core.adapter.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.request.RefreshTokenRequest;
import com.example.demo.core.application.dto.request.RegisterRequest;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.dto.response.RefreshTokenResponse;
import com.example.demo.core.application.dto.response.common.BaseResponse;
import com.example.demo.core.application.service.AuthService;
import com.example.demo.core.domain.helper.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final AuthHelper authHelper;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ResponseUtils.success("Register Success"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        var response = authService.login(
                request, authHelper.getClientIp(httpRequest)
        );
        return ResponseEntity.ok(ResponseUtils.success(response, "Login Success"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<RefreshTokenResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        var response = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseUtils.success("Token Refresh Success"));
    }


}
