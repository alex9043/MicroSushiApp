package ru.alex9043.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.productservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}