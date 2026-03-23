package com.example.demo.core.domain.repository;

import com.example.demo.core.application.dto.projection.MenuFlatProjection;
import com.example.demo.core.application.dto.projection.MenuOnlyProjection;
import com.example.demo.core.domain.model.entity.Menu;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
            AND m.is_deleted = false
            GROUP BY m.id, m.code, m.name, m.sort_order, m.parent_id
            ORDER BY m.sort_order;
            """, nativeQuery = true)
    List<MenuFlatProjection> findMenuByAccount(String accId);

    Optional<Menu> findByCodeAndDeletedIsFalse(@NotBlank String code);

    @Query("""
        SELECT m.id as menuId,
               m.code as menuCode,
               m.name as menuName,
               m.sortOrder as sortOrder,
               m.active as active,
               m.parent.id as parentId
        FROM Menu m
        WHERE m.isDeleted = false
        """)
    Page<MenuOnlyProjection> findMenus(Pageable pageable);

}
