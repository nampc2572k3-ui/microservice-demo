package com.example.demo.profile.model.entity.actor;

import com.example.demo.common.constant.Gender;
import com.example.demo.common.constant.RelationshipStatus;
import com.example.demo.common.constant.UserProfileType;
import com.example.demo.profile.model.entity.relationship.actor.Follow;
import com.example.demo.profile.model.entity.relationship.actor.Friend;
import com.example.demo.profile.model.entity.relationship.actor.MemberOf;
import com.example.demo.profile.model.entity.relationship.user.*;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("user")
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

    // Work and education
    @Relationship(type = "WORK_AT")
    private Set<WorkAt> workPlaces;

    @Relationship(type = "STUDY_AT")
    private Set<StudyAt> studyAts;

    // Interests and preferences
    @Relationship(type = "HAS_HOBBY")
    private Set<HasHobby> hobbies;

    @Relationship(type = "INTERESTED_IN")
    private Set<InterestedIn> interests;

    // Link
    @Relationship(type = "HAS_LINK")
    private Set<HasLink> links;

    // travel
    @Relationship(type = "TRAVELED_TO")
    private Set<TraveledTo> traveledTo;

    // img todo

    // relationships
        @Relationship(type = "FRIEND_WITH")
        private Set<Friend> friends;

        @Relationship(type = "FOLLOWS")
        private Set<Follow> follows;

        @Relationship(type = "MEMBER_OF")
        private Set<MemberOf> memberOf;




}
