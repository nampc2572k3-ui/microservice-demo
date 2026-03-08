package com.example.demo.profile.model.entity.reference;

import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("place")
public class Place extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private LocalDate date;

    // url location todo
}
