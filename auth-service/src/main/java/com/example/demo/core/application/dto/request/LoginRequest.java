package com.example.demo.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    private String identity;

    @NotBlank(message = "Password is required")
    private String password;

    private String deviceId;

    private String deviceName;

    private String platform;

}
