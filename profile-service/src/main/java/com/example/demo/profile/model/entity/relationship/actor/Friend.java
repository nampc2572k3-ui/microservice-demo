package com.example.demo.profile.model.entity.relationship.actor;

import com.example.demo.profile.model.entity.BaseEntity;
import com.example.demo.profile.model.entity.actor.UserProfile;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RelationshipProperties
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private UserProfile user;
}
