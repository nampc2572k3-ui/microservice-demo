package com.example.demo.profile.model.entity.actor;


import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("groups")
public class Group extends BaseActor {

    // img todo

}
