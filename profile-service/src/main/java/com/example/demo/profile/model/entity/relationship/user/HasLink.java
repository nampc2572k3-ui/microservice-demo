package com.example.demo.profile.model.entity.relationship.user;

import com.example.demo.profile.model.entity.BaseEntity;
import com.example.demo.profile.model.entity.actor.attribute.Link;
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
public class HasLink extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Link link;
}
