package ru.alex9043.cartservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.cartservice.model.Cart;
import ru.alex9043.cartservice.model.CartItem;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartAndProductId(Cart cart, UUID productId);

    Set<CartItem> findAllByCart(Cart cart);
}