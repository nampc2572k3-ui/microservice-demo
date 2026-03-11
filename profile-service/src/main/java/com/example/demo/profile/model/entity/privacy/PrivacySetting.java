package com.example.demo.profile.model.entity.privacy;

import com.example.demo.profile.model.entity.Audit;
import com.example.demo.profile.model.entity.enums.PrivacyLevel;
import com.example.demo.profile.model.entity.enums.ProfileSection;
import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("privacy_setting")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PrivacySetting extends Audit {

    @Id
    @GeneratedValue
    private Long id;

    private ProfileSection section;

    private PrivacyLevel level;
}
