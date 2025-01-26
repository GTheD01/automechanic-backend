package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.CarBrandRequest;
import com.popeftimov.automechanic.dto.CarBrandResponse;
import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.dto.CarRequest;
import com.popeftimov.automechanic.service.CarModelService;
import com.popeftimov.automechanic.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.popeftimov.automechanic.service.CarBrandService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CarController {

    private final CarBrandService carBrandService;
    private final CarModelService carModelService;
    private final CarService carService;

    @GetMapping("/brands")
    public List<CarBrandResponse> getCarBrands() {
        return carBrandService.getAllCarBrands();
    }

    @GetMapping("/{brand}/models")
    public List<CarModelResponse> getCarModelsByBrand(@PathVariable String brand) {
        return carModelService.getAllCarModelsByBrand(brand.toUpperCase());
    }

    @PostMapping("/admin/brands")
    public ResponseEntity<?> createCarBrand(@RequestBody CarBrandRequest carBrandRequest) {
        return carBrandService.createCarBrand(carBrandRequest.getBrand());
    }

    @GetMapping("/admin/{brandName}/create-model")
    public ResponseEntity<?> createCarModel(@PathVariable String brandName, String modelName) {
        return carModelService.createModel(brandName, modelName);
    }

    @PostMapping("/cars")
    public ResponseEntity<?> addCar(@RequestBody CarRequest carRequest) {
        return carService.addCar(carRequest);
    }

    @GetMapping("admin/cars/user/{userId}")
    public ResponseEntity<?> getUserCars(@PathVariable Long userId) {
        return carService.getUserCars(userId);
    }

    @GetMapping("/cars")
    public ResponseEntity<?> getLoggedInUserCars() {
        return carService.getLoggedInUserCars();
    }
}
