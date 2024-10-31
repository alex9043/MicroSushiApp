package ru.alex9043.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.productservice.dto.ProductRequestDto;
import ru.alex9043.productservice.dto.ProductsResponseDto;
import ru.alex9043.productservice.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ProductsResponseDto getProducts() {
        log.info("Received request to get all products.");
        ProductsResponseDto response = productService.getProducts();
        log.info("Returning {} products.", response.getProducts().size());
        return response;
    }

    @GetMapping("{productId}")
    public ProductsResponseDto.ProductResponseDto getProduct(@PathVariable("productId") UUID productId) {
        log.info("Received request to get product with ID: {}", productId);
        ProductsResponseDto.ProductResponseDto product = productService.getProduct(productId);
        log.debug("Product details: {}", product);
        return product;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductsResponseDto createProduct(@Valid @RequestBody ProductRequestDto productRequestDTO) {
        log.info("Received request to create a new product: {}", productRequestDTO.getName());
        ProductsResponseDto response = productService.createProduct(productRequestDTO);
        log.info("Product created successfully: {}", productRequestDTO.getName());
        return response;
    }

    @PutMapping("{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductsResponseDto updateProduct(@PathVariable("productId") UUID productId, @Valid @RequestBody ProductRequestDto productRequestDTO) {
        log.info("Received request to update product with ID: {}", productId);
        ProductsResponseDto response = productService.updateProduct(productId, productRequestDTO);
        log.info("Product with ID: {} updated successfully.", productId);
        return response;
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductsResponseDto deleteProduct(@PathVariable("productId") UUID productId) {
        log.info("Received request to delete product with ID: {}", productId);
        ProductsResponseDto response = productService.deleteProduct(productId);
        log.info("Product with ID: {} deleted successfully.", productId);
        return response;
    }
}