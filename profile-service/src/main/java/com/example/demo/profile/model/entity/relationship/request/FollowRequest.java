package com.example.demo.profile.model.entity.relationship.request;

import com.example.demo.common.constant.RequestStatus;
import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.actor.BaseActor;
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
public class FollowRequest extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private RequestStatus status;

    @TargetNode
    private BaseActor target;
}
