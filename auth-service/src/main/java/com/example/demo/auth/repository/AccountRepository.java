package com.example.demo.auth.repository;

import com.example.demo.auth.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a WHERE a.username = :input OR a.email = :input")
    Optional<Account> findByUsernameOrEmail(@Param("input") String input);


    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM accounts a
        JOIN user_role ur ON a.id = ur.user_id
        JOIN roles r ON ur.role_id = r.id
        JOIN role_menu rm ON r.id = rm.role_id
        WHERE (a.username = :username OR a.email = :username)
          AND rm.menu_id = :menuId
          AND (rm.permission & :action) = :action
        """,
            nativeQuery = true)
    boolean existsByUsernameAndMenuIdAndPermission(
            @Param("username") String username,
            @Param("menuId") Long menuId,
            @Param("action") Integer action
    );
}
