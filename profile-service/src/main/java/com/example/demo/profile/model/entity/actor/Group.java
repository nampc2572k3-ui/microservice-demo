package com.example.demo.profile.model.entity.actor;


import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("group")
public class Group extends BaseActor {

    private String description;

    // img todo

}
