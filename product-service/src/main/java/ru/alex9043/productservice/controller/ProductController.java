package ru.alex9043.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.productservice.dto.ProductRequestDTO;
import ru.alex9043.productservice.dto.ProductsResponseDto;
import ru.alex9043.productservice.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ProductsResponseDto getProducts() {
        return productService.getProducts();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ProductsResponseDto createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO);
    }
}
