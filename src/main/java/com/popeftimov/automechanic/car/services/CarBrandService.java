package com.popeftimov.automechanic.car.services;

import com.popeftimov.automechanic.car.dto.CarBrandResponse;
import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.repository.CarBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    public List<CarBrandResponse> getAllCarBrands() {
        return carBrandRepository.findAll().stream()
                .map(carBrand -> new CarBrandResponse(carBrand.getId(), carBrand.getName()))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> createCarBrand(String brandName) {
        if (brandName == null || brandName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CarBrand carBrand = new CarBrand(brandName);
        carBrandRepository.save(carBrand);
        CarBrandResponse carBrandResponse = new CarBrandResponse(carBrand.getId(), carBrand.getName());
        return ResponseEntity.ok(carBrandResponse);
    }
}
