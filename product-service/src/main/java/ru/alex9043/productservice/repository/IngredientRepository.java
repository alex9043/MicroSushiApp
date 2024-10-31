package ru.alex9043.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.productservice.model.Ingredient;

import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
}