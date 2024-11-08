package ru.alex9043.addressservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alex9043.addressservice.model.District;
import ru.alex9043.addressservice.repository.DistrictRepository;

import java.util.List;

@Configuration
@Slf4j
public class DataInitializer {
    @Bean
    public CommandLineRunner initDistricts(DistrictRepository districtRepository) {
        return args -> {
            if (districtRepository.existsByName("Выборгский") ||
                    districtRepository.existsByName("Калининский") ||
                    districtRepository.existsByName("Центральный") ||
                    districtRepository.existsByName("Приморский")) {
                District district1 = new District();
                District district2 = new District();
                District district3 = new District();
                District district4 = new District();
                district1.setName("Выборгский");
                district2.setName("Калининский");
                district3.setName("Центральный");
                district4.setName("Приморский");
                districtRepository.saveAll(List.of(district1, district2, district3, district4));
                log.info("Districts created successfully");
            } else {
                log.info("Districts already exists, skipping creation.");
            }
        };
    }
}
