package com.popeftimov.automechanic.car.services;

import com.popeftimov.automechanic.car.dto.CarBrandResponse;
import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.repository.CarBrandRepository;
import lombok.RequiredArgsConstructor;
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

    public CarBrand getCarBrand(String name) {
        return carBrandRepository.findByName(name);
    }

    public CarBrand createCarBrand(CarBrand carBrand) {
        return carBrandRepository.save(carBrand);
    }
}
