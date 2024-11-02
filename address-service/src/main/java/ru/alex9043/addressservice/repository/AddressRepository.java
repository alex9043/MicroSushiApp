package ru.alex9043.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alex9043.addressservice.model.Address;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}