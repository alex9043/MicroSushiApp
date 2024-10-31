package ru.alex9043.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.productservice.dto.CategoriesResponseDto;
import ru.alex9043.productservice.dto.CategoryRequestDto;
import ru.alex9043.productservice.model.Category;
import ru.alex9043.productservice.repository.CategoryRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public Category findById(UUID id) {
        log.debug("Finding category with ID: {}", id);
        return categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category not found by ID: " + id)
        );
    }

    public CategoriesResponseDto getCategories() {
        log.info("Fetching all categories.");
        Set<CategoriesResponseDto.CategoryResponseDto> categories = categoryRepository.findAll().stream()
                .map(c -> modelMapper.map(c, CategoriesResponseDto.CategoryResponseDto.class))
                .collect(Collectors.toSet());
        return new CategoriesResponseDto(categories);
    }

    public CategoriesResponseDto.CategoryResponseDto getCategory(UUID id) {
        log.info("Fetching category details for ID: {}", id);
        Category category = findById(id);
        return modelMapper.map(category, CategoriesResponseDto.CategoryResponseDto.class);
    }

    public CategoriesResponseDto createCategory(CategoryRequestDto categoryRequestDTO) {
        log.info("Creating category with name: {}", categoryRequestDTO.getName());
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        categoryRepository.save(category);
        log.info("Category created successfully with name: {}", category.getName());
        return getCategories();
    }

    public CategoriesResponseDto updateCategory(UUID id, CategoryRequestDto categoryRequestDTO) {
        log.info("Updating category with ID: {}", id);
        Category category = findById(id);
        category.setName(categoryRequestDTO.getName());
        categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", id);
        return getCategories();
    }

    public CategoriesResponseDto deleteCategory(UUID id) {
        log.info("Deleting category with ID: {}", id);
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with ID: {}", id);
        return getCategories();
    }
}