package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.request.LoginRequest;
import com.example.demo.core.application.dto.request.RefreshTokenRequest;
import com.example.demo.core.application.dto.request.RegisterRequest;
import com.example.demo.core.application.dto.response.LoginResponse;
import com.example.demo.core.application.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthService {
    void register(@Valid RegisterRequest request);

    LoginResponse login(@Valid LoginRequest request, HttpServletRequest httpRequest);

    RefreshTokenResponse refreshToken(@Valid RefreshTokenRequest request);
}
