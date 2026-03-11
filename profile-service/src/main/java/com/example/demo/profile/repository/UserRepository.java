package com.example.demo.profile.repository;

import com.example.demo.profile.model.entity.actor.User;
import com.example.demo.profile.model.projection.UserProfileProjection;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("""
            MATCH (u:User {slug: $slug})
            // privacy section
            OPTIONAL MATCH (u)-[:HAS_PRIVACY_SETTING]->(ps:PrivacySetting)
            // work of user
            OPTIONAL MATCH (u)-[:WORKED_AT]->(w:WorkAt)-[:AT]->(p:Page)
            RETURN u {
                .id,
                .name,
                .dob,
                .location,
                .hometown,
                .email,
                .phone,
                privacySettings: collect(ps {
                    .section,
                    .level
                }),
                works: collect(w {
                    .jobTitle,
                    .startDate,
                    .endDate,
                    .location,
                    .description,
                    .privacyLevel,
                    page: p {
                        .id,
                        .name,
                        .slug
                    }
                })
            } AS user
            """
    )
    UserProfileProjection findUserProfileBySlug(String slug);
}
