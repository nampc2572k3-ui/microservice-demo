package com.example.demo.profile.model.entity.relationship.user;

import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.actor.Page;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@RelationshipProperties
public class WorkAt extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private String jobTitle;

    private LocalDate startDate;

    private LocalDate endDate;

    private String location;

    private String description;

    @TargetNode
    private Page page;
}
