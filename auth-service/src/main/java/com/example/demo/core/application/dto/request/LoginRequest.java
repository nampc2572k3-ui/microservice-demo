package com.example.demo.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    private String identity;

    @NotBlank(message = "Password is required")
    private String password;

    // Device information for security and push notifications
    private long deviceId;

    private String deviceName;

    @Pattern(regexp = "IOS|ANDROID|WEB", message = "Platform must be IOS, ANDROID or WEB")
    private String platform;

    private String pushToken;

}
