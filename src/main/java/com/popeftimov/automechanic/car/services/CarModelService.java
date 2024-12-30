package com.popeftimov.automechanic.car.services;

import com.popeftimov.automechanic.car.dto.CarModelResponse;
import com.popeftimov.automechanic.car.dto.CarModelYearsResponse;
import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.models.CarModel;
import com.popeftimov.automechanic.car.repository.CarBrandRepository;
import com.popeftimov.automechanic.car.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarBrandRepository carBrandRepository;

    public ResponseEntity<?> createModel(@PathVariable String brandName, String modelName) {
        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            return ResponseEntity.notFound().build();
        }
        CarModel carModel = new CarModel(modelName, carBrand);
        carModelRepository.save(carModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(carModel);
    }

    public ResponseEntity<?> addYearToCarModel(String modelName, int year) {
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            return ResponseEntity.notFound().build();
        }
        carModel.addYear(year);
        carModelRepository.save(carModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(carModel);
    }

    public List<CarModelResponse> getAllCarModelsByBrand(String brand) {
        CarBrand carBrand = carBrandRepository.findByName(brand);
        return carModelRepository.findByBrand(carBrand)
                .stream().map(carModel -> new CarModelResponse(carModel.getId(), carModel.getName()))
                .collect(Collectors.toList());
    }

    public CarModelYearsResponse getModelYearsByName(String modelName) {
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel != null) {
            return new CarModelYearsResponse(carModel.getYears());
        } else {
            return null;
        }
    }
}
