package ru.alex9043.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.addressservice.model.District;

import java.util.UUID;

public interface DistrictRepository extends JpaRepository<District, UUID> {
    boolean existsByName(String name);
}