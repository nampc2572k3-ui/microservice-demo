package com.example.demo.profile.model.entity.actor;


import com.example.demo.profile.model.entity.relationship.actor.HasPrivacy;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("group")
public class Group extends BaseActor {

    private String description;

    // privacy
    @Relationship(type = "HAS_PRIVACY")
    private HasPrivacy hasPrivacy;

    // img todo

}
