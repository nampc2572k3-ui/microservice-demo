package com.example.demo.profile.model.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("user-profile")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Property("acc_id")
    private UUID accountId;

    @Property("last_name")
    private String lastName;

    @Property("first_name")
    private String firstName;

    @Property("full_name")
    private String fullName;

    @Property("dob")
    private LocalDate dob;

}
