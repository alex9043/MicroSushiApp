package ru.alex9043.addressservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.alex9043.addressservice.dto.AddressRequestDto;
import ru.alex9043.addressservice.dto.AddressesResponseDto;
import ru.alex9043.addressservice.model.Address;
import ru.alex9043.addressservice.repository.AddressRepository;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final DistrictService districtService;
    private final RabbitService rabbitService;
    private final ModelMapper modelMapper;

    private boolean isAdmin() {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        log.debug("User admin status: {}", isAdmin);
        return isAdmin;
    }

    private UUID getAccountID(String token) {
        token = token.substring(7);
        SubjectResponseDto account = rabbitService.getSubject(new TokenRequestDto(token));
        log.debug("Fetched account ID: {}", account.getId());
        return account.getId();
    }

    public AddressesResponseDto getAddresses(String token) {
        log.info("Fetching addresses for the user.");
        if (isAdmin()) {
            log.info("Admin user detected, fetching all addresses.");
            Set<AddressesResponseDto.AddressResponseDto> addresses = addressRepository.findAll().stream()
                    .map(address -> modelMapper.map(address, AddressesResponseDto.AddressResponseDto.class))
                    .collect(Collectors.toSet());
            log.info("Fetched {} addresses for admin.", addresses.size());
            return new AddressesResponseDto(addresses);
        } else {
            try {
                UUID id = getAccountID(token);
                log.debug("Fetching addresses for user with ID: {}", id);
                Set<AddressesResponseDto.AddressResponseDto> addresses = addressRepository.findAllByAccountId(id).stream()
                        .map(address -> modelMapper.map(address, AddressesResponseDto.AddressResponseDto.class))
                        .collect(Collectors.toSet());
                log.info("Fetched {} addresses for user ID: {}", addresses.size(), id);
                return new AddressesResponseDto(addresses);
            } catch (Exception e) {
                log.error("Failed to fetch addresses: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to fetch addresses");
            }
        }
    }

    public AddressesResponseDto.AddressResponseDto getAddress(UUID addressId, String token) {
        log.info("Fetching address with ID: {}", addressId);
        if (isAdmin()) {
            log.info("Admin user detected, fetching address directly.");
            return modelMapper.map(addressRepository.findById(addressId), AddressesResponseDto.AddressResponseDto.class);
        } else {
            try {
                UUID id = getAccountID(token);
                log.debug("Fetching address for user with ID: {}", id);
                Address address = addressRepository.findById(addressId).orElseThrow(() -> {
                    log.error("Address not found with ID: {}", addressId);
                    return new IllegalArgumentException("Address not found");
                });
                if (!address.getAccountId().equals(id)) {
                    log.error("Unauthorized access to address ID: {}", addressId);
                    throw new SecurityException("Unauthorized access");
                }
                log.info("Fetched address with ID: {}", addressId);
                return modelMapper.map(address, AddressesResponseDto.AddressResponseDto.class);
            } catch (Exception e) {
                log.error("Failed to fetch address: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to fetch address");
            }
        }
    }

    public AddressesResponseDto createAddress(AddressRequestDto addressRequestDTO, String token) {
        log.info("Creating new address.");
        if (isAdmin()) {
            log.info("Admin user detected, creating address with specified account ID.");
            Address address = new Address();
            address.setId(null);
            address.setDistrict(districtService.findById(addressRequestDTO.getDistrictId()));
            address.setAccountId(addressRequestDTO.getAccountId());
            saveAddressFields(addressRequestDTO, address);
        } else {
            try {
                UUID id = getAccountID(token);
                if (!addressRequestDTO.getAccountId().equals(id)) {
                    log.error("Unauthorized access to address ID: {}", addressRequestDTO.getAccountId());
                    throw new SecurityException("Unauthorized access");
                }
                Address address = new Address();
                address.setId(null);
                address.setDistrict(districtService.findById(addressRequestDTO.getDistrictId()));
                address.setAccountId(id);
                saveAddressFields(addressRequestDTO, address);
            } catch (Exception e) {
                log.error("Failed to create address: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to create address");
            }
        }
        log.info("Address created successfully.");
        return getAddresses(token);
    }

    public AddressesResponseDto updateAddress(UUID addressId, AddressRequestDto addressRequestDTO, String token) {
        log.info("Updating address with ID: {}", addressId);
        if (isAdmin()) {
            log.info("Admin user detected, updating address.");
            Address address = addressRepository.findById(addressId).orElseThrow(() -> {
                log.error("Address not found with ID: {}", addressId);
                return new IllegalArgumentException("Address not found");
            });
            address.setDistrict(districtService.findById(addressRequestDTO.getDistrictId()));
            saveAddressFields(addressRequestDTO, address);
        } else {
            try {
                Address address = checkAddressForAccount(addressId, token);
                address.setDistrict(districtService.findById(addressRequestDTO.getDistrictId()));
                saveAddressFields(addressRequestDTO, address);
            } catch (Exception e) {
                log.error("Failed to update address: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to update address");
            }
        }
        log.info("Address with ID: {} updated successfully.", addressId);
        return getAddresses(token);
    }

    private void saveAddressFields(AddressRequestDto addressRequestDTO, Address address) {
        log.debug("Saving fields for address: {}", addressRequestDTO);
        address.setName(addressRequestDTO.getName());
        address.setStreet(addressRequestDTO.getStreet());
        address.setHouseNumber(addressRequestDTO.getHouseNumber());
        address.setBuilding(addressRequestDTO.getBuilding());
        address.setEntrance(addressRequestDTO.getEntrance());
        address.setFloor(addressRequestDTO.getFloor());
        address.setApartmentNumber(addressRequestDTO.getApartmentNumber());
        addressRepository.save(address);
        log.info("Address fields saved successfully for: {}", address.getId());
    }

    public AddressesResponseDto deleteAddress(UUID addressId, String token) {
        log.info("Deleting address with ID: {}", addressId);
        if (isAdmin()) {
            log.info("Admin user detected, deleting address.");
            addressRepository.deleteById(addressId);
        } else {
            try {
                Address address = checkAddressForAccount(addressId, token);
                addressRepository.delete(address);
            } catch (Exception e) {
                log.error("Failed to delete address: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to delete address");
            }
        }
        log.info("Address with ID: {} deleted successfully.", addressId);
        return getAddresses(token);
    }

    private Address checkAddressForAccount(UUID addressId, String token) {
        log.debug("Checking address ownership for ID: {}", addressId);
        Address address = addressRepository.findById(addressId).orElseThrow(() -> {
            log.error("Address not found with ID: {}", addressId);
            return new IllegalArgumentException("Address not found");
        });
        if (!address.getAccountId().equals(getAccountID(token))) {
            log.error("Unauthorized access to address ID: {}", addressId);
            throw new SecurityException("Unauthorized access");
        }
        return address;
    }
}
