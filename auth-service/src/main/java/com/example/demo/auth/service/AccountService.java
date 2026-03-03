package com.example.demo.auth.service;

import com.example.demo.auth.model.dto.request.RegisterRequest;
import jakarta.validation.Valid;

public interface AccountService {
    void registerAccount(@Valid RegisterRequest request);
}
