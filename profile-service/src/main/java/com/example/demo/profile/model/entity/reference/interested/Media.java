package com.example.demo.profile.model.entity.reference.interested;

import com.example.demo.common.constant.MediaType;
import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("media")
public class Media  extends Audit implements InterestTarget {

    @Id
    @GeneratedValue
    private Long id;

    private MediaType mediaType;

    // img todo

}
