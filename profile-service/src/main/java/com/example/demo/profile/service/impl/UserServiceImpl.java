package com.example.demo.profile.service.impl;

import com.example.demo.common.constant.ErrorCode;
import com.example.demo.common.exception.CustomBusinessException;
import com.example.demo.profile.model.dto.response.UserProfileResponse;
import com.example.demo.profile.model.projection.UserProfileProjection;
import com.example.demo.profile.repository.UserRepository;
import com.example.demo.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Cacheable(
            value = "userProfile",
            key = "T(com.example.demo.common.constant.CacheKey).getUserProfileKey(#slug)"
    )
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileBySlug(String slug) {
        UserProfileProjection userProfileDao = userRepository.findUserProfileBySlug(slug);

        UserProfileResponse userProfile = UserProfileResponse.from(userProfileDao);

        log.info("User profile for slug '{}' retrieved from database: {}", slug, userProfile);

        if (userProfile == null) {
            log.warn("User profile for slug '{}' not found in database", slug);
            throw new CustomBusinessException(
                    ErrorCode.USER_NOT_FOUND.getCode(),
                    ErrorCode.USER_NOT_FOUND.getMessage()
            );
        }

        return userProfile;
    }

}
