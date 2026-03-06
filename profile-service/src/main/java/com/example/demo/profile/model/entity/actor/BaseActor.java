package com.example.demo.profile.model.entity.actor;


import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BaseActor extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String bio;


}
