package com.example.demo.profile.helper;

import com.example.demo.profile.model.entity.enums.PrivacyLevel;
import com.example.demo.profile.model.entity.enums.ProfileSection;
import com.example.demo.profile.model.projection.PrivacyProjection;

import java.util.List;

public class PrivacyHelper {

    public static boolean isPublicSection(
            List<PrivacyProjection> settings,
            ProfileSection section
    ) {

        if (settings == null) return true;

        return settings.stream()
                .filter(p -> p.getSection() == section)
                .findFirst()
                .map(p -> p.getLevel() == PrivacyLevel.PUBLIC)
                .orElse(true);
    }

}
