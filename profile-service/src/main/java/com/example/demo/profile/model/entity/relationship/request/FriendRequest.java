package com.example.demo.profile.model.entity.relationship.request;


import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.actor.User;
import com.example.demo.profile.model.entity.enums.RequestStatus;
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
public class FriendRequest extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private RequestStatus status;

    @TargetNode
    private User target;
}
