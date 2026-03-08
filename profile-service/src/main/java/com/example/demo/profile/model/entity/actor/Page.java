package com.example.demo.profile.model.entity.actor;

import com.example.demo.common.constant.PageType;
import com.example.demo.profile.model.entity.reference.interested.InterestTarget;
import com.example.demo.profile.model.entity.relationship.actor.HasPrivacy;
import com.example.demo.profile.model.entity.relationship.user.HasLink;
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
@Node("page")
public class Page extends BaseActor implements InterestTarget {

    private PageType pageType;

    // Contact information
    private String phone;

    private String email;

    // Time active
    private String openTime;

    // Location information
    private String location;

    // Link todo
    @Relationship(type = "HAS_LINK")
    private Set<HasLink> links;

    // Evaluate todo

    // Price todo

    // Service todo
    private String service;

    // privacy
    @Relationship(type = "HAS_PRIVACY")
    private HasPrivacy hasPrivacy;

    // img todo

}
