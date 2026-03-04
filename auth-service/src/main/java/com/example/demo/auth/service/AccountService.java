package com.example.demo.auth.service;

import com.example.demo.auth.model.dto.request.RegisterRequest;
import com.example.demo.auth.model.dto.response.AccountDetailResponse;
import com.example.demo.auth.model.dto.request.LoginRequest;

public interface AccountService {
    void registerAccount(RegisterRequest request);

    AccountDetailResponse login(LoginRequest request);
}
