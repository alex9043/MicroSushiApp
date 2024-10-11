package ru.alex9043.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.productservice.dto.ProductRequestDTO;
import ru.alex9043.productservice.dto.ProductResponseDto;
import ru.alex9043.productservice.dto.ProductsResponseDto;
import ru.alex9043.productservice.model.Product;
import ru.alex9043.productservice.repository.ProductRepository;

import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductsResponseDto createProduct(ProductRequestDTO productRequestDTO) {
        Product product = modelMapper.map(productRequestDTO, Product.class);
        log.info("Request - {}", productRequestDTO.toString());
        log.info("Product - {}", product.toString());
        saveProductFields(productRequestDTO, product);
        return getProducts();
    }

    private void saveProductFields(ProductRequestDTO productRequestDTO, Product product) {
        byte[] imageBytes = new byte[0];
        if (productRequestDTO.getBase64Image() != null) {
            imageBytes = Base64.getDecoder().decode(productRequestDTO.getBase64Image());
        }
        product.setImage(imageBytes);
        productRepository.save(product);
    }

    public ProductsResponseDto getProducts() {
        Set<ProductResponseDto> products = productRepository.findAll().stream().map(
                p -> modelMapper.map(p, ProductResponseDto.class)
        ).collect(Collectors.toSet());

        return ProductsResponseDto.builder()
                .products(products)
                .build();
    }
}
