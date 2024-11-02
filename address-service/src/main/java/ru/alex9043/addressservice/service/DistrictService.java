package ru.alex9043.addressservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.addressservice.dto.DistrictRequestDto;
import ru.alex9043.addressservice.dto.DistrictsResponseDto;
import ru.alex9043.addressservice.model.District;
import ru.alex9043.addressservice.repository.DistrictRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final ModelMapper modelMapper;

    public DistrictsResponseDto getDistricts() {
        log.info("Fetching all districts from the database.");
        Set<DistrictsResponseDto.DistrictResponseDto> districts = districtRepository.findAll().stream()
                .map(district -> modelMapper.map(district, DistrictsResponseDto.DistrictResponseDto.class))
                .collect(Collectors.toSet());
        log.info("Fetched {} districts.", districts.size());
        return new DistrictsResponseDto(districts);
    }

    public DistrictsResponseDto.DistrictResponseDto getDistrict(UUID id) {
        log.info("Fetching district with ID: {}", id);
        District district = districtRepository.findById(id).orElseThrow(() -> {
            log.error("District not found with ID: {}", id);
            return new IllegalArgumentException("District not found");
        });
        log.debug("District details: {}", district);
        return modelMapper.map(district, DistrictsResponseDto.DistrictResponseDto.class);
    }

    public DistrictsResponseDto createDistrict(DistrictRequestDto districtRequestDto) {
        log.info("Creating district with name: {}", districtRequestDto.getName());
        District district = new District();
        saveDistrictFields(districtRequestDto, district);
        log.info("District created: {}", districtRequestDto.getName());
        return getDistricts();
    }

    public DistrictsResponseDto updateDistrict(UUID id, DistrictRequestDto districtRequestDto) {
        log.info("Updating district with ID: {}", id);
        District district = districtRepository.findById(id).orElseThrow(() -> {
            log.error("District not found with ID: {}", id);
            return new IllegalArgumentException("District not found");
        });
        saveDistrictFields(districtRequestDto, district);
        log.info("District with ID: {} updated successfully.", id);
        return getDistricts();
    }

    public DistrictsResponseDto deleteDistrict(UUID id) {
        log.info("Deleting district with ID: {}", id);
        districtRepository.deleteById(id);
        log.info("District with ID: {} deleted successfully.", id);
        return getDistricts();
    }

    private void saveDistrictFields(DistrictRequestDto districtRequestDto, District district) {
        log.debug("Saving fields for district: {}", districtRequestDto);
        district.setName(districtRequestDto.getName());
        districtRepository.save(district);
        log.info("District fields saved for: {}", district.getName());
    }

    public District findById(UUID id) {
        log.info("Finding district");
        return districtRepository.findById(id).orElseThrow(
                () -> new RuntimeException("District not found by ID: " + id)
        );
    }
}
