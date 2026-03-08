package com.example.demo.profile.model.entity.reference;

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
@Node("link")
public class Link extends Audit {

    @Id
    @GeneratedValue
    private Long id;


    private String title;

    private String url;
}
