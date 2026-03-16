package com.example.demo.domain.repository;

import com.example.demo.domain.model.dto.projection.MenuFlatProjection;
import com.example.demo.domain.model.dto.projection.MenuResourceFlatProjection;
import com.example.demo.domain.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query(value = """
            SELECT
                 m.id as id,
                 m.code as code,
                 m.name as name,
                 m.sort_order as sortOrder,
                 m.parent_id as parentId,
                 BIT_OR(rm.bitmask) as bitmask
            FROM account_roles ar
                    JOIN role_menus rm ON rm.role_id = ar.role_id
                    JOIN menus m ON m.id = rm.menu_id
            WHERE ar.account_id = :accId
            AND m.is_active = true
            GROUP BY m.id, m.code, m.name, m.sort_order, m.parent_id
            ORDER BY m.sort_order;
            """, nativeQuery = true)
    List<MenuFlatProjection> findMenuByAccount(String accId);


    @Query("""
            SELECT 
                m.id as menuId,
                m.code as menuCode,
                m.name as menuName,
                m.sortOrder as sortOrder,
                m.active as active,
                m.parent.id as parentId,
            
                r.id as resourceId,
                r.pathPattern as pathPattern,
                r.httpMethod as httpMethod,
                r.description as description,
                r.action as action,
                r.active as resourceActive
            FROM Menu m
            LEFT JOIN m.resources r
            ORDER BY m.sortOrder
            """)
    List<MenuResourceFlatProjection> findAllMenuWithResources();
}
