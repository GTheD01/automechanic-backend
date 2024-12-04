package com.popeftimov.automechanic.car.services;

import com.popeftimov.automechanic.car.dto.CarBrandResponse;
import com.popeftimov.automechanic.car.dto.CarModelResponse;
import com.popeftimov.automechanic.car.dto.CarModelYearsResponse;
import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.models.CarModel;
import com.popeftimov.automechanic.car.repository.CarBrandRepository;
import com.popeftimov.automechanic.car.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarBrandRepository carBrandRepository;

    public CarModel getCarModel(String name) {
        return carModelRepository.findByName(name);
    }

    public CarModel createModel(CarModel carModel) {
        return carModelRepository.save(carModel);
    }

    public CarModel addYearToCarModel(CarModel carModel, int year) {
        carModel.addYear(year);
        return carModelRepository.save(carModel);
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
