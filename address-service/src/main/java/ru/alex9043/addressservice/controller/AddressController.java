package ru.alex9043.addressservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.addressservice.dto.AddressRequestDto;
import ru.alex9043.addressservice.dto.AddressesResponseDto;
import ru.alex9043.addressservice.service.AddressService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Address Management", description = "Управление адресами")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Получить список всех адресов",
            description = "Возвращает список всех адресов для администратора или только адреса текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список адресов успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressesResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    public AddressesResponseDto getAddresses(
            @RequestHeader("Authorization") String token) {
        log.info("Received request to get addresses.");
        return addressService.getAddresses(token);
    }

    @Operation(summary = "Получить адрес по ID",
            description = "Возвращает данные об адресе по указанному ID для администратора или если адрес принадлежит пользователю.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные об адресе успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressesResponseDto.AddressResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Адрес не найден", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("{addressId}")
    public AddressesResponseDto.AddressResponseDto getAddress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "UUID адреса", required = true)
            @PathVariable("addressId") UUID addressId) {
        log.info("Received request to get address with ID: {}", addressId);
        return addressService.getAddress(addressId, token);
    }

    @Operation(summary = "Создать новый адрес",
            description = "Позволяет создать новый адрес для пользователя.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Адрес успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressesResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные адреса", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public AddressesResponseDto createAddress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Данные нового адреса", required = true)
            @Valid @RequestBody AddressRequestDto addressRequestDTO) {
        log.info("Received request to create a new address for user ID: {}", addressRequestDTO.getAccountId());
        AddressesResponseDto response = addressService.createAddress(addressRequestDTO, token);
        log.info("Address created successfully for user ID: {}", addressRequestDTO.getAccountId());
        return response;
    }

    @Operation(summary = "Обновить адрес по ID",
            description = "Позволяет обновить данные об адресе по указанному ID, если адрес принадлежит пользователю или администратору.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адрес успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressesResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Адрес не найден", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("{addressId}")
    public AddressesResponseDto updateAddress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "UUID адреса", required = true)
            @PathVariable("addressId") UUID addressId,
            @Parameter(description = "Обновленные данные адреса", required = true)
            @Valid @RequestBody AddressRequestDto addressRequestDTO) {
        log.info("Received request to update address with ID: {}", addressId);
        return addressService.updateAddress(addressId, addressRequestDTO, token);
    }

    @Operation(summary = "Удалить адрес по ID",
            description = "Позволяет удалить адрес по указанному ID, если адрес принадлежит пользователю или администратору.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адрес успешно удален",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressesResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Адрес не найден", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("{addressId}")
    public AddressesResponseDto deleteAddress(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "UUID адреса", required = true)
            @PathVariable("addressId") UUID addressId) {
        log.info("Received request to delete address with ID: {}", addressId);
        return addressService.deleteAddress(addressId, token);
    }
}
