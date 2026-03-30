package com.example.demo.core.persistence;

import com.example.demo.core.application.dto.projection.MenuResourceFlatProjection;
import com.example.demo.core.application.dto.projection.ResourcePermissionProjection;
import com.example.demo.core.application.dto.response.ResourceResponse;
import com.example.demo.core.domain.model.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
        SELECT new com.example.demo.core.application.dto.response.ResourceResponse(
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


    @Query(value = """
        SELECT r.path_pattern AS pathPattern,
               r.http_method AS httpMethod,
               rm.bitmask AS bitmask
        FROM account_roles ar
        JOIN role_menus rm ON ar.role_id = rm.role_id
        JOIN menus m ON rm.menu_id = m.id
        JOIN menu_resources mr ON m.id = mr.menu_id
        JOIN resources r ON rm.resource_id = r.id
        WHERE ar.account_id = :accId
    """, nativeQuery = true)
    List<ResourcePermissionProjection> findAllByAccount(@Param("accId") String accId);
}
