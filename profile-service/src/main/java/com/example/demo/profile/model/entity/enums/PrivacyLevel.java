package com.example.demo.profile.model.entity.enums;

import lombok.Getter;

@Getter
public enum PrivacyLevel {
    PUBLIC(0),
    PRIVATE(1),
//    MEMBERS_ONLY(1),
    FRIENDS_ONLY(2);

    private final int level;

    PrivacyLevel(int level) {
        this.level = level;
    }

}
