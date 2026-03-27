package com.example.demo.core.persistence;

import com.example.demo.core.domain.model.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT a FROM Account a WHERE a.username = :input OR a.email = :indentity")
    Optional<Account> findByUsernameOrEmail(@Param("indentity") String indentity);

    boolean existsAccountByEmail(@NotBlank @Email(message = "Invalid email format") String email);

    boolean existsAccountByUsername(@NotBlank @Size(min = 3, max = 50, message = "Username must be 3-50 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, underscores") String username);
}
