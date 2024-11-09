package ru.alex9043.cartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.cartservice.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findCartByAccountId(UUID accountId);

    void deleteByAccountId(UUID accountId);
}