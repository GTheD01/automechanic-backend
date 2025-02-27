package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarBrandRepository carBrandRepository;

    public CarModelResponse convertCarModelToCarModelResponse(CarModel carModel) {
        return new CarModelResponse(
                carModel.getId(),
                carModel.getName()
        );
    }

    public ResponseEntity<?> createModel(@PathVariable String brandName, String modelName) {
        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            return ResponseEntity.notFound().build();
        }
        CarModel carModel = new CarModel(modelName, carBrand);
        carModelRepository.save(carModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(carModel);
    }

    public List<CarModelResponse> getAllCarModelsByBrand(String brand) {
        CarBrand carBrand = carBrandRepository.findByName(brand);
        return carModelRepository.findByBrand(carBrand)
                .stream().map(this::convertCarModelToCarModelResponse)
                .toList();
    }
}
