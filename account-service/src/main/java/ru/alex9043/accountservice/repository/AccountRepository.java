package ru.alex9043.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.accountservice.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);

    Optional<Account> findByRefreshTokenContains(String refreshToken);
}