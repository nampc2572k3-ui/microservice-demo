package com.example.demo.profile.model.entity.actor;


import com.example.demo.profile.model.entity.relationship.actor.HasPrivacy;
import com.example.demo.profile.model.entity.relationship.request.FollowRequest;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("Group")
public class Group extends BaseActor {

    private String description;

    // privacy
    @Relationship(type = "HAS_PRIVACY")
    private HasPrivacy hasPrivacy;

    @Relationship(type = "FOLLOW_REQUEST")
    private Set<FollowRequest> followRequests;
    // img todo

}
