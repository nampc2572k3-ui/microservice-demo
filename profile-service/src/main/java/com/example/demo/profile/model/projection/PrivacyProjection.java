package com.example.demo.profile.model.projection;

import com.example.demo.profile.model.entity.enums.PrivacyLevel;
import com.example.demo.profile.model.entity.enums.ProfileSection;

public interface PrivacyProjection {

    ProfileSection getSection();

    PrivacyLevel getLevel();
}
