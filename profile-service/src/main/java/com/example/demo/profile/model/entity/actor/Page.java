package com.example.demo.profile.model.entity.actor;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node("page")
public class Page extends BaseActor {

    // Base information
    private String Location;

    // Contact information
    private String phone;

    private String email;

    // Time active
    private String openTime;

    // Location information
    private String location;

    // Link todo

    // Evaluate todo

    // Price todo

    // Service todo

    // Team Member todo

    // Blue checkmark todo

    // img todo

}
