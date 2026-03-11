package com.example.demo.profile.model.dto.response;

import com.example.demo.profile.helper.PrivacyHelper;
import com.example.demo.profile.model.entity.enums.ProfileSection;
import com.example.demo.profile.model.projection.UserProfileProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserProfileResponse {

    // basic info
    private Long id;
    private String name;

    // birth
    private LocalDate dob;

    // location info
    private String location;
    private String hometown;

    // contact info
    private String email;
    private String phone;


    public static UserProfileResponse from(UserProfileProjection projection) {

        if (projection == null) {
            return null;
        }

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(projection.getId())
                .name(projection.getName());

        var privacy = projection.getPrivacySettings();

        // check privacy settings before setting sensitive info

        // dob privacy
        if (PrivacyHelper.isPublicSection(privacy, ProfileSection.DOB_INFO)) {
            builder.dob(projection.getDob());
        }

        // location privacy
        if (PrivacyHelper.isPublicSection(privacy, ProfileSection.LOCATION_INFO)) {
            builder.location(projection.getLocation());
            builder.hometown(projection.getHometown());
        }

        // location privacy
        if (PrivacyHelper.isPublicSection(privacy, ProfileSection.CONTACT_INFO)) {
            builder.email(projection.getEmail());
            builder.phone(projection.getPhone());
        }

        return builder.build();
    }

}
