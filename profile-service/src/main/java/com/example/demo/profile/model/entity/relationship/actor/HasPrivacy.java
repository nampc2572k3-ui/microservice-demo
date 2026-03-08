package com.example.demo.profile.model.entity.relationship.actor;

import com.example.demo.profile.model.entity.privacy.PrivacySetting;
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
public class HasPrivacy  {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private PrivacySetting privacySetting;

}
