package com.example.demo.profile.model.entity.actor;

import com.example.demo.common.constant.Gender;
import com.example.demo.common.constant.RelationshipStatus;
import com.example.demo.common.constant.UserProfileType;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("user-profile")
public class UserProfile extends BaseActor {

    private UUID accId;

    private UserProfileType userProfileType;

    // Personal information
    private String firstName;

    private String lastName;

    private LocalDate dob;

    private Gender gender;

    private RelationshipStatus relationshipStatus;

    // Location information
    private String location;

    private String hometown;

    // Contact information
    private String email;

    private String phone;

    // Work and education todo

    // Interests and preferences todo

    // Link todo

    // travel todo

    // img todo

}
