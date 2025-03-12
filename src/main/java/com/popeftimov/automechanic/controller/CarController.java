package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.service.CarModelService;
import com.popeftimov.automechanic.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/admin/brands")
    public ResponseEntity<Page<CarBrandResponse>> getAdminCarBrands(Pageable pageable) {
        Page<CarBrandResponse> carBrandResponses = carBrandService.getAdminCarBrands(pageable);
        return ResponseEntity.ok(carBrandResponses);
    }

    @PostMapping("/admin/brands")
    public ResponseEntity<CarBrandResponse> addCarBrand(@RequestBody CarBrandRequest carBrandRequest) {
        return carBrandService.createCarBrand(carBrandRequest.getBrandName());
    }

    @DeleteMapping("/admin/brands/{brandName}")
    public ResponseEntity<Void> deleteCarBrand(@PathVariable("brandName") String brandName) {
        carBrandService.deleteCarBrand(brandName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{brandName}/create-model")
    public ResponseEntity<CarModelResponse> createCarBrandModel(
            @PathVariable("brandName") String brandName, @RequestBody CarModelRequest carModelRequest) {
        return carModelService.createCarBrandModel(brandName, carModelRequest.getModelName());
    }

    @GetMapping("admin/cars/user/{userId}")
    public ResponseEntity<List<CarResponse>> getUserCars(@PathVariable("userId") Long userId) {
        return carService.getUserCars(userId);
    }

    @GetMapping("/admin/car-models")
    public ResponseEntity<Page<CarModelResponse>> getCarModels(Pageable pageable) {
        Page<CarModelResponse> carModelResponseList = carModelService.getAllCarModels(pageable);
        return ResponseEntity.ok(carModelResponseList);
    }

    @GetMapping("/brands")
    public List<CarBrandResponse> getCarBrands() {
        return carBrandService.getAllCarBrands();
    }

    @GetMapping("/{brand}/models")
    public List<CarModelResponse> getCarModelsByBrand(@PathVariable("brand") String brand) {
        return carModelService.getAllCarModelsByBrand(brand.toUpperCase());
    }

    @PostMapping("/cars")
    public ResponseEntity<CarResponse> addCar(@RequestBody CarRequest carRequest) {
        return carService.addCar(carRequest);
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> getLoggedInUserCars() {
        return carService.getLoggedInUserCars();
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<?> getCar(@PathVariable("carId") Long carId) {
        return carService.getCar(carId);
    }

    @PutMapping("/cars/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable("carId") Long carId, @RequestBody CarRequest carRequest) {
        return carService.updateCar(carId, carRequest);
    }

    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<?> deleteCar(@PathVariable("carId") Long carId) {
        return carService.deleteCar(carId);
    }
}
