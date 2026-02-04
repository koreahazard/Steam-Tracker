package com.example.steam_tracker.account.repository;

import com.example.steam_tracker.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<Account> findByUsername(String username);

}
