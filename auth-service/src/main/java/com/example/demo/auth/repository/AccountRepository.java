package com.example.demo.auth.repository;

import com.example.demo.auth.model.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    boolean existsAccountByUsername(@NotBlank String userName);

    boolean existsAccountByEmail(@NotBlank @Email String email);
}
