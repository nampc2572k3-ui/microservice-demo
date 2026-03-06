package com.example.demo.profile.model.entity.actor.attribute;

import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("hobby")
public class Hobby extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String comment;
}
