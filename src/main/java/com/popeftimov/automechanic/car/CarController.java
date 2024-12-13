package com.popeftimov.automechanic.car;

import com.popeftimov.automechanic.car.dto.CarBrandRequest;
import com.popeftimov.automechanic.car.dto.CarBrandResponse;
import com.popeftimov.automechanic.car.dto.CarModelResponse;
import com.popeftimov.automechanic.car.dto.CarModelYearsResponse;
import com.popeftimov.automechanic.car.services.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.popeftimov.automechanic.car.services.CarBrandService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CarController {

    private final CarBrandService carBrandService;
    private final CarModelService carModelService;

    @GetMapping("/public/brands")
    public List<CarBrandResponse> getCarBrands() {
        return carBrandService.getAllCarBrands();
    }

    @GetMapping("/public/{brand}/models")
    public List<CarModelResponse> getCarModelsByBrand(@PathVariable String brand) {
        return carModelService.getAllCarModelsByBrand(brand.toUpperCase());
    }

    @GetMapping("/public/{brand}/models/{modelName}/years")
    public CarModelYearsResponse getModelYears(@PathVariable String brand, @PathVariable String modelName) {
        return carModelService.getModelYearsByName(modelName);
    }

    @PostMapping("/admin/brands")
    public ResponseEntity<?> createCarBrand(@RequestBody CarBrandRequest carBrandRequest) {
        return carBrandService.createCarBrand(carBrandRequest.getBrand());
    }

    @GetMapping("/admin/{brandName}/create-model")
    public ResponseEntity<?> createCarModel(@PathVariable String brandName, String modelName) {
        return carModelService.createModel(brandName, modelName);
    }
}
