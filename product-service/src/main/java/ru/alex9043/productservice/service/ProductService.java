package ru.alex9043.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.productservice.dto.ProductRequestDto;
import ru.alex9043.productservice.dto.ProductsResponseDto;
import ru.alex9043.productservice.model.Product;
import ru.alex9043.productservice.repository.ProductRepository;

import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final IngredientService ingredientService;
    private final CategoryService categoryService;

    public ProductsResponseDto createProduct(ProductRequestDto productRequestDTO) {
        log.info("Creating product with name: {}", productRequestDTO.getName());
        Product product = new Product();
        saveProductFields(productRequestDTO, product);
        log.info("Product created: {}", productRequestDTO.getName());
        return getProducts();
    }

    private void saveProductFields(ProductRequestDto productRequestDTO, Product product) {
        log.debug("Saving fields for product: {}", productRequestDTO);
        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());

        byte[] imageBytes = new byte[0];
        if (productRequestDTO.getBase64Image() != null) {
            try {
                imageBytes = Base64.getDecoder().decode(productRequestDTO.getBase64Image());
            } catch (IllegalArgumentException e) {
                log.error("Failed to decode image for product: {}", productRequestDTO.getName(), e);
            }
        }
        product.setImage(imageBytes);
        if (!productRequestDTO.getIngredients().isEmpty()) {
            product.setIngredients(productRequestDTO.getIngredients().stream().map(
                    ingredientService::findById
            ).collect(Collectors.toSet()));
        }
        if (!productRequestDTO.getCategories().isEmpty()) {
            product.setCategories(productRequestDTO.getCategories().stream().map(
                    categoryService::findById
            ).collect(Collectors.toSet()));
        }

        productRepository.save(product);
        log.info("Product fields saved for: {}", product.getName());
    }

    public ProductsResponseDto getProducts() {
        log.info("Fetching all products from the database.");
        Set<ProductsResponseDto.ProductResponseDto> products = productRepository.findAll().stream().map(
                p -> modelMapper.map(p, ProductsResponseDto.ProductResponseDto.class)
        ).collect(Collectors.toSet());
        log.info("Fetched {} products.", products.size());
        return new ProductsResponseDto(products);
    }

    public ProductsResponseDto.ProductResponseDto getProduct(UUID id) {
        log.info("Fetching product with ID: {}", id);
        ProductsResponseDto.ProductResponseDto product = modelMapper.map(productRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Product not found with ID: {}", id);
                    return new IllegalArgumentException("Product not found");
                }
        ), ProductsResponseDto.ProductResponseDto.class);
        log.debug("Product details: {}", product);
        return product;
    }

    public ProductsResponseDto updateProduct(UUID id, ProductRequestDto productRequestDTO) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Product not found with ID: {}", id);
                    return new IllegalArgumentException("Product not found");
                }
        );
        saveProductFields(productRequestDTO, product);
        log.info("Product with ID: {} updated successfully.", id);
        return getProducts();
    }

    public ProductsResponseDto deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        log.info("Product with ID: {} deleted successfully.", id);
        return getProducts();
    }
}