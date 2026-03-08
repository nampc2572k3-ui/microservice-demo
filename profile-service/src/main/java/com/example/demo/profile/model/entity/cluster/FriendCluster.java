package com.example.demo.profile.model.entity.cluster;

import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.actor.User;
import com.example.demo.profile.model.entity.relationship.actor.Friend;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("friend_cluster")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FriendCluster extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private String shard;

    @Relationship(type = "CLUSTER_OF", direction = Relationship.Direction.INCOMING)
    private User owner;

    @Relationship(type = "FRIEND")
    private Set<Friend> friends;

}
