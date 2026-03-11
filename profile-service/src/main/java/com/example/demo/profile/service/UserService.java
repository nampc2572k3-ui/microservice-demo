package com.example.demo.profile.service;

import com.example.demo.profile.model.dto.response.UserProfileResponse;
import com.example.demo.profile.model.projection.UserProfileProjection;

public interface UserService {

    UserProfileResponse getUserProfileBySlug(String slug);

}
