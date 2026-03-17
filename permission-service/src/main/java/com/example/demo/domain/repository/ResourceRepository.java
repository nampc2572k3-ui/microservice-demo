package com.example.demo.domain.repository;

import com.example.demo.domain.model.dto.projection.MenuResourceFlatProjection;
import com.example.demo.domain.model.dto.response.ResourceResponse;
import com.example.demo.domain.model.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
        SELECT new com.example.demo.domain.model.dto.response.ResourceResponse(
            r.id,
            r.pathPattern,
            r.httpMethod,
            r.description,
            CAST(r.action as string),
            r.active
        )
        FROM Resource r
        WHERE r.isDeleted = false
        AND r.active = true
        """)
    Page<ResourceResponse> findAllResourceResponses(Pageable pageable);

    @Query("""
        SELECT 
            m.id as menuId,

            r.id as resourceId,
            r.pathPattern as pathPattern,
            r.httpMethod as httpMethod,
            r.description as description,
            r.action as action,
            r.active as resourceActive

        FROM Menu m
        LEFT JOIN m.resources r
        WHERE m.id IN :menuIds
        AND r.active = true
        """)
    List<MenuResourceFlatProjection> findResourcesByMenuIds(List<Long> menuIds);

    boolean existsByHttpMethodAndPathPattern(String httpMethod, String pathPattern);
}
