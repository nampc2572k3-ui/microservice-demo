package com.example.demo.profile.model.entity.actor;

import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseActor extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String bio;

    private boolean isVerified;

}
