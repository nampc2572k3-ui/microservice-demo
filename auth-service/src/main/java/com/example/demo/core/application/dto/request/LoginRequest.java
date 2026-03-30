package com.example.demo.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    private String identity;

    @NotBlank(message = "Password is required")
    private String password;

    // Device information for security and push notifications
    @Positive(message = "Device ID must be greater than 0")
    private long deviceId;

    @NotBlank(message = "Device name is required")
    @Size(min = 1, max = 100, message = "Device name must be 1-100 characters")
    private String deviceName;

    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "IOS|ANDROID|WEB", message = "Platform must be IOS, ANDROID or WEB")
    private String platform;

    @NotBlank(message = "Push token is required")
    @Size(min = 10, max = 500, message = "Push token must be 10-500 characters")
    private String pushToken;

}
