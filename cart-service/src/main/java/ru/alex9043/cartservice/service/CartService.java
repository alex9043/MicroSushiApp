package ru.alex9043.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.cartservice.dto.CartResponseDto;
import ru.alex9043.cartservice.dto.ProductItemRequestDto;
import ru.alex9043.cartservice.model.Cart;
import ru.alex9043.cartservice.model.CartItem;
import ru.alex9043.cartservice.repository.CartItemRepository;
import ru.alex9043.cartservice.repository.CartRepository;
import ru.alex9043.commondto.TokenRequestDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RabbitService rabbitService;
    private final ModelMapper modelMapper;

    public CartResponseDto getCart(String authorization) {
        UUID accountId = getAccountId(authorization);
        log.debug("Getting cart for account ID: {}", accountId);
        Cart cart = findOrCreateCart(accountId);

        CartResponseDto response = modelMapper.map(cart, CartResponseDto.class);
        Set<CartItem> items = cartItemRepository.findAllByCart(cart);
        response.setCartElements(items.stream().map(
                item -> modelMapper.map(item, CartResponseDto.CartElementResponseDto.class)
        ).collect(Collectors.toSet()));
        return response;
    }

    private Cart findOrCreateCart(UUID accountId) {
        return cartRepository.findCartByAccountId(accountId).orElseGet(() -> {
            log.info("Cart not found for account ID: {}. Creating new cart.", accountId);
            Cart cart = new Cart();
            cart.setAccountId(accountId);
            return cartRepository.save(cart);
        });
    }

    public UUID getAccountId(String authorization) {
        UUID accountId = rabbitService.getSubject(new TokenRequestDto(authorization.substring(7))).getId();
        log.debug("Extracted account ID: {} from authorization token", accountId);
        return accountId;
    }

    public CartResponseDto addCartItemIntoCart(String authorization, ProductItemRequestDto productItemRequestDto) {
        if (productItemRequestDto.getProductId() == null) {
            log.error("Product ID is null in request. Cannot add item to cart.");
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        UUID accountId = getAccountId(authorization);
        Cart cart = findOrCreateCart(accountId);
        log.info("Adding item to cart. Account ID: {}, Product ID: {}", accountId, productItemRequestDto.getProductId());

        CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productItemRequestDto.getProductId())
                .orElseGet(() -> {
                    log.info(
                            "Product not found in cart. Adding new item for Product ID: {}",
                            productItemRequestDto.getProductId());
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductId(productItemRequestDto.getProductId());
                    newItem.setQuantity(0);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItemRepository.save(cartItem);
        log.info(
                "Updated item quantity in cart. Product ID: {}, New Quantity: {}",
                productItemRequestDto.getProductId(), cartItem.getQuantity());

        return getCart(authorization);
    }

    public CartResponseDto removeCartItemFromCart(String authorization, ProductItemRequestDto productItemRequestDto) {
        UUID accountId = getAccountId(authorization);
        Cart cart = cartRepository.findCartByAccountId(accountId).orElseThrow(
                () -> {
                    log.error("Cart not found for account ID: {}", accountId);
                    return new IllegalArgumentException("User not found");
                }
        );

        CartItem cartItem = cartItemRepository
                .findByCartAndProductId(cart, productItemRequestDto.getProductId()).orElseThrow(
                        () -> {
                            log.error(
                                    "Product ID: {} not found in cart for account ID: {}",
                                    productItemRequestDto.getProductId(), accountId);
                            return new IllegalArgumentException("Product not found in cart");
                        }
                );

        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
            log.info(
                    "Decreased quantity for Product ID: {}. New Quantity: {}"
                    , productItemRequestDto.getProductId(), cartItem.getQuantity());
        } else {
            cartItemRepository.delete(cartItem);
            log.info(
                    "Removed Product ID: {} from cart for account ID: {}"
                    , productItemRequestDto.getProductId(), accountId);
            if (cart.getCartItems().isEmpty()) {
                cartRepository.delete(cart);
                log.info("Deleted empty cart for account ID: {}", accountId);
                return null;
            }
        }
        return getCart(authorization);
    }

    public void deleteCart(String authorization) {
        UUID accountId = getAccountId(authorization);
        cartRepository.deleteByAccountId(accountId);
        log.info("Deleted cart for account ID: {}", accountId);
    }
}
