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
import ru.alex9043.addressservice.dto.DistrictRequestDto;
import ru.alex9043.addressservice.dto.DistrictsResponseDto;
import ru.alex9043.addressservice.service.DistrictService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses/districts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "District Management", description = "Управление районами")
public class DistrictController {

    private final DistrictService districtService;

    @Operation(summary = "Получить список всех районов",
            description = "Возвращает список всех районов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список районов успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DistrictsResponseDto.class)))
    })
    @GetMapping
    public DistrictsResponseDto getDistricts() {
        log.info("Получен запрос на получение всех районов.");
        DistrictsResponseDto response = districtService.getDistricts();
        log.info("Возвращено {} районов.", response.getDistricts().size());
        return response;
    }

    @Operation(summary = "Получить район по ID",
            description = "Возвращает данные о районе по указанному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные о районе успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DistrictsResponseDto.DistrictResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Район не найден", content = @Content)
    })
    @GetMapping("{districtId}")
    public DistrictsResponseDto.DistrictResponseDto getDistrict(
            @Parameter(description = "UUID района", required = true)
            @PathVariable("districtId") UUID districtId) {
        log.info("Получен запрос на получение района с ID: {}", districtId);
        DistrictsResponseDto.DistrictResponseDto district = districtService.getDistrict(districtId);
        log.debug("Детали района: {}", district);
        return district;
    }

    @Operation(summary = "Создать новый район",
            description = "Позволяет администратору создать новый район.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Район успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DistrictsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные района", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public DistrictsResponseDto createDistrict(
            @Parameter(description = "Данные нового района", required = true)
            @Valid @RequestBody DistrictRequestDto districtRequestDto) {
        log.info("Получен запрос на создание нового района с названием: {}", districtRequestDto.getName());
        DistrictsResponseDto response = districtService.createDistrict(districtRequestDto);
        log.info("Район с названием {} успешно создан.", districtRequestDto.getName());
        return response;
    }

    @Operation(summary = "Обновить район по ID",
            description = "Позволяет администратору обновить данные о районе по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Район успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DistrictsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Район не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{districtId}")
    public DistrictsResponseDto updateDistrict(
            @Parameter(description = "UUID района", required = true)
            @PathVariable("districtId") UUID districtId,
            @Parameter(description = "Обновленные данные района", required = true)
            @Valid @RequestBody DistrictRequestDto districtRequestDto) {
        log.info("Получен запрос на обновление района с ID: {}", districtId);
        DistrictsResponseDto response = districtService.updateDistrict(districtId, districtRequestDto);
        log.info("Район с ID: {} успешно обновлен.", districtId);
        return response;
    }

    @Operation(summary = "Удалить район по ID",
            description = "Позволяет администратору удалить район по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Район успешно удален",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DistrictsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Район не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{districtId}")
    public DistrictsResponseDto deleteDistrict(
            @Parameter(description = "UUID района", required = true)
            @PathVariable("districtId") UUID districtId) {
        log.info("Получен запрос на удаление района с ID: {}", districtId);
        DistrictsResponseDto response = districtService.deleteDistrict(districtId);
        log.info("Район с ID: {} успешно удален.", districtId);
        return response;
    }
}