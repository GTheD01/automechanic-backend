package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.exception.CarExceptions;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ResponseEntity<CarModelResponse> createCarBrandModel(@PathVariable String brandName, String modelName) {
        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            throw new CarExceptions.CarBrandNotFound();
        }
        CarModel existingCarModel = carModelRepository.findByName(modelName);

        if (existingCarModel != null) {
            throw new CarExceptions.CarModelExists(modelName);
        }

        CarModel carModel = new CarModel(modelName, carBrand);
        carModelRepository.save(carModel);

        CarModelResponse carModelResponse = this.convertCarModelToCarModelResponse(carModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(carModelResponse);
    }

    public List<CarModelResponse> getAllCarModelsByBrand(String brandName) {

        CarBrand carBrand = carBrandRepository.findByName(brandName);

        return carModelRepository.findByBrand(carBrand)
                .stream().map(this::convertCarModelToCarModelResponse)
                .toList();
    }

    public Page<CarModelResponse> getAllCarModels(Pageable pageable) {
        Page<CarModel> carModelList = carModelRepository.findAll(pageable);
        return carModelList.map(this::convertCarModelToCarModelResponse);
    }

    public ResponseEntity<?> deleteCarModel(String modelName) {
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            throw new CarExceptions.CarModelNotFound();
        }
        carModelRepository.delete(carModel);
        return ResponseEntity.noContent().build();
    }
}
