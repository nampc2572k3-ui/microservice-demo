package com.example.demo.auth.service;

import com.example.demo.auth.model.dto.request.RegisterRequest;
import com.example.demo.auth.model.dto.response.AccountDetailResponse;
import com.example.demo.auth.model.dto.request.LoginRequest;
import com.example.demo.auth.model.dto.request.RefreshTokenRequest;
import com.example.demo.auth.model.dto.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AccountService {
    void registerAccount(RegisterRequest request);

    AccountDetailResponse login(LoginRequest request);

    void logout(HttpServletRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
