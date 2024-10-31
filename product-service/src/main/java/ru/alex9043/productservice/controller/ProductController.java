package ru.alex9043.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product Management", description = "Управление продуктами")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Получить список всех продуктов",
            description = "Возвращает список всех продуктов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список продуктов успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductsResponseDto.class)))
    })
    @GetMapping
    public ProductsResponseDto getProducts() {
        log.info("Received request to get all products.");
        ProductsResponseDto response = productService.getProducts();
        log.info("Returning {} products.", response.getProducts().size());
        return response;
    }

    @Operation(summary = "Получить продукт по ID",
            description = "Возвращает данные о продукте по указанному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные о продукте успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductsResponseDto.ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Продукт не найден", content = @Content)
    })
    @GetMapping("{productId}")
    public ProductsResponseDto.ProductResponseDto getProduct(
            @Parameter(description = "UUID продукта", required = true)
            @PathVariable("productId") UUID productId) {
        log.info("Received request to get product with ID: {}", productId);
        ProductsResponseDto.ProductResponseDto product = productService.getProduct(productId);
        log.debug("Product details: {}", product);
        return product;
    }

    @Operation(summary = "Создать новый продукт",
            description = "Позволяет администратору создать новый продукт.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Продукт успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные продукта", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ProductsResponseDto createProduct(
            @Parameter(description = "Данные нового продукта", required = true)
            @Valid @RequestBody ProductRequestDto productRequestDTO) {
        log.info("Received request to create a new product: {}", productRequestDTO.getName());
        ProductsResponseDto response = productService.createProduct(productRequestDTO);
        log.info("Product created successfully: {}", productRequestDTO.getName());
        return response;
    }

    @Operation(summary = "Обновить продукт по ID",
            description = "Позволяет администратору обновить данные о продукте по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{productId}")
    public ProductsResponseDto updateProduct(
            @Parameter(description = "UUID продукта", required = true)
            @PathVariable("productId") UUID productId,
            @Parameter(description = "Обновленные данные продукта", required = true)
            @Valid @RequestBody ProductRequestDto productRequestDTO) {
        log.info("Received request to update product with ID: {}", productId);
        ProductsResponseDto response = productService.updateProduct(productId, productRequestDTO);
        log.info("Product with ID: {} updated successfully.", productId);
        return response;
    }

    @Operation(summary = "Удалить продукт по ID",
            description = "Позволяет администратору удалить продукт по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно удален",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{productId}")
    public ProductsResponseDto deleteProduct(
            @Parameter(description = "UUID продукта", required = true)
            @PathVariable("productId") UUID productId) {
        log.info("Received request to delete product with ID: {}", productId);
        ProductsResponseDto response = productService.deleteProduct(productId);
        log.info("Product with ID: {} deleted successfully.", productId);
        return response;
    }
}