package ru.alex9043.productservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alex9043.productservice.model.Category;
import ru.alex9043.productservice.model.Ingredient;
import ru.alex9043.productservice.model.Product;
import ru.alex9043.productservice.repository.CategoryRepository;
import ru.alex9043.productservice.repository.IngredientRepository;
import ru.alex9043.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepository,
            IngredientRepository ingredientRepository,
            CategoryRepository categoryRepository) {
        return args -> {
            if (
                    productRepository.findByName("Скучный ролл").isEmpty() &&
                            productRepository.findByName("Ролл с лососем").isEmpty() &&
                            productRepository.findByName("Ролл со всем").isEmpty()
            ) {
                log.info("Initializing...");
                initIngredients(ingredientRepository);
                initCategories(categoryRepository);
                initProducts(productRepository, categoryRepository, ingredientRepository);
            } else {
                log.info("Data already initialized.");
            }
        };
    }

    private void initProducts(ProductRepository productRepository, CategoryRepository categoryRepository, IngredientRepository ingredientRepository) {
        log.info("Initializing products...");
        Product product1 = new Product();
        Product product2 = new Product();
        Product product3 = new Product();

        product1.setName("Скучный ролл");
        product1.setPrice(BigDecimal.valueOf(250));
        product1.setIngredients(Set.of(
                ingredientRepository.findByName("Рис").orElseThrow(),
                ingredientRepository.findByName("Нори").orElseThrow()));
        product1.setCategories(Set.of(categoryRepository.findByName("Новинка").orElseThrow()));

        product2.setName("Ролл с лососем");
        product2.setPrice(BigDecimal.valueOf(400));
        product2.setIngredients(Set.of(
                ingredientRepository.findByName("Рис").orElseThrow(),
                ingredientRepository.findByName("Нори").orElseThrow(),
                ingredientRepository.findByName("Лосось").orElseThrow()));
        product2.setCategories(Set.of(
                categoryRepository.findByName("С лососем").orElseThrow(),
                categoryRepository.findByName("Новинка").orElseThrow()));

        product3.setName("Ролл со всем");
        product3.setPrice(BigDecimal.valueOf(650));
        product3.setIngredients(Set.copyOf(ingredientRepository.findAll()));
        product3.setCategories(Set.copyOf(categoryRepository.findAll()));

        try {
            productRepository.saveAll(List.of(product1, product2, product3));
            log.info("Products initialized successfully.");
        } catch (Exception e) {
            log.error("Error initializing products: {}", e.getMessage());
        }
    }

    private void initCategories(CategoryRepository categoryRepository) {
        log.info("Initializing categories...");
        Category category1 = new Category();
        Category category2 = new Category();
        Category category3 = new Category();
        Category category4 = new Category();

        category1.setName("Новинка");
        category2.setName("Острое");
        category3.setName("С лососем");
        category4.setName("С угрем");

        try {
            categoryRepository.saveAll(List.of(category1, category2, category3, category4));
            log.info("Categories initialized successfully.");
        } catch (Exception e) {
            log.error("Error initializing categories: {}", e.getMessage());
        }
    }

    private void initIngredients(IngredientRepository ingredientRepository) {
        log.info("Initializing ingredients...");
        Ingredient ingredient1 = new Ingredient();
        Ingredient ingredient2 = new Ingredient();
        Ingredient ingredient3 = new Ingredient();
        Ingredient ingredient4 = new Ingredient();
        Ingredient ingredient5 = new Ingredient();

        ingredient1.setName("Рис");
        ingredient2.setName("Нори");
        ingredient3.setName("Спайси соус");
        ingredient4.setName("Лосось");
        ingredient5.setName("Угорь");
        try {
            ingredientRepository.saveAll(List.of(ingredient1, ingredient2, ingredient3, ingredient4, ingredient5));
            log.info("Ingredients initialized successfully.");
        } catch (Exception e) {
            log.error("Error initializing ingredients: {}", e.getMessage());
        }
    }
}
