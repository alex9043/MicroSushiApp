package ru.alex9043.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.productservice.model.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
}