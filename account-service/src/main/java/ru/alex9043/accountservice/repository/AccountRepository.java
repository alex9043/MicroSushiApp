package ru.alex9043.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.accountservice.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByPhone(String phone);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByName(String name);

    Optional<Account> findByRefreshTokenContains(String refreshToken);
}