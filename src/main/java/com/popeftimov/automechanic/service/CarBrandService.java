package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarBrandResponse;
import com.popeftimov.automechanic.exception.CarExceptions;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarBrandService {

    private final CarBrandRepository carBrandRepository;

    public CarBrandResponse convertCarBrandToCarBrandResponse(CarBrand carBrand) {
        return new CarBrandResponse(
                carBrand.getId(),
                carBrand.getName()
        );
    };

    public Page<CarBrandResponse> getAdminCarBrands(Pageable pageable) {
        Page<CarBrand> carBrands = carBrandRepository.findAll(pageable);

        return carBrands.map(this::convertCarBrandToCarBrandResponse);
    }

    public List<CarBrandResponse> getAllCarBrands() {
        return carBrandRepository.findAll().stream()
                .map(this::convertCarBrandToCarBrandResponse)
                .toList();
    }

    public ResponseEntity<?> createCarBrand(String brandName) {
        if (brandName == null || brandName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CarBrand existingCarBrand = carBrandRepository.findByName(brandName);
        if (existingCarBrand != null) {
            throw new CarExceptions.CarBrandExists(brandName);
        }

        CarBrand carBrand = new CarBrand(brandName);
        carBrandRepository.save(carBrand);
        CarBrandResponse carBrandResponse = this.convertCarBrandToCarBrandResponse(carBrand);
        return ResponseEntity.ok(carBrandResponse);
    }

    public void deleteCarBrand(String brandName) {
        CarBrand carBrand = carBrandRepository.findByName(brandName);
        carBrandRepository.delete(carBrand);
    }
}
