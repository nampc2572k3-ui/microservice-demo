package com.example.demo.profile.controller;

import com.example.demo.common.utils.ResponseUtils;
import com.example.demo.profile.model.dto.response.BaseResponse;
import com.example.demo.profile.model.dto.response.UserProfileResponse;
import com.example.demo.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/profile/{slug}")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getUserProfile(@PathVariable String slug) {
        var response = userService.getUserProfileBySlug(slug);
        return ResponseEntity.ok(
                ResponseUtils.success(response, "User profile retrieved successfully")
        );
    }


}
