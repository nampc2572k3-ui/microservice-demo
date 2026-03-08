package com.example.demo.profile.model.entity.relationship.user;

import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.actor.User;
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
public class HasRelationship extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private User user;

}
