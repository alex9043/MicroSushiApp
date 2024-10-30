package ru.alex9043.accountservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initAdmin(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.findByEmail("admin@examle.com").isEmpty()) {
                Account admin = new Account();
                admin.setName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPhone("+71234567890");
                admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
                admin.setPassword(passwordEncoder.encode("adminPass"));
                admin.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

                accountRepository.save(admin);
            }
        };
    }
}
