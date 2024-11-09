package ru.alex9043.cartservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.cartservice.dto.CartResponseDto;
import ru.alex9043.cartservice.dto.ProductItemRequestDto;
import ru.alex9043.cartservice.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart Management", description = "Управление корзинами покупателей")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponseDto getCart(@RequestHeader("Authorization") String authorization) {
        log.info("Received request to get cart for authorization: {}", authorization);
        return cartService.getCart(authorization);
    }

    @PostMapping("add")
    public CartResponseDto addCartItemIntoCart(@RequestHeader("Authorization") String authorization,
                                               @RequestBody ProductItemRequestDto productItemRequestDto) {
        log.info(
                "Received request to add item to cart for authorization: {}, product ID: {}",
                authorization, productItemRequestDto.getProductId());
        return cartService.addCartItemIntoCart(authorization, productItemRequestDto);
    }

    @PostMapping("remove")
    public CartResponseDto removeCartItemFromCart(@RequestHeader("Authorization") String authorization,
                                                  @RequestBody ProductItemRequestDto productItemRequestDto) {
        log.info("Received request to remove item from cart for authorization: {}, product ID: {}",
                authorization, productItemRequestDto.getProductId());
        return cartService.removeCartItemFromCart(authorization, productItemRequestDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@RequestHeader("Authorization") String authorization) {
        log.info("Received request to delete cart for authorization: {}", authorization);
        cartService.deleteCart(authorization);
    }
}
