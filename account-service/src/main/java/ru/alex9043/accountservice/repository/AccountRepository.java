package ru.alex9043.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.accountservice.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}