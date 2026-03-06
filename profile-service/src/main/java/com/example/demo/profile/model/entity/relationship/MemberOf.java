package com.example.demo.profile.model.entity.relationship;

import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RelationshipProperties
public class MemberOf extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
}
