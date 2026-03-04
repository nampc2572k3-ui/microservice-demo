package com.example.demo.auth.controller;


import com.example.demo.auth.model.dto.request.RegisterRequest;
import com.example.demo.auth.model.dto.response.AccountDetailResponse;
import com.example.demo.auth.model.dto.response.BaseResponse;
import com.example.demo.auth.model.dto.request.LoginRequest;
import com.example.demo.auth.service.AccountService;
import com.example.demo.common.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        accountService.registerAccount(request);
        return ResponseEntity.ok(
                ResponseUtils.success("Account registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AccountDetailResponse>> loginUser(
            @Valid @RequestBody LoginRequest request
            ) {
        return ResponseEntity.ok(
                ResponseUtils.success(
                        accountService.login(request)
                        , "Login successful"
                )
        );
    }

}
