package com.example.demo.profile.model.entity.relationship.user;

import com.example.demo.profile.model.entity.reference.Hobby;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RelationshipProperties
public class HasHobby  {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Hobby hobby;
}
