package com.popeftimov.automechanic.car;

import com.popeftimov.automechanic.car.dto.CarBrandResponse;
import com.popeftimov.automechanic.car.dto.CarModelResponse;
import com.popeftimov.automechanic.car.dto.CarModelYearsResponse;
import com.popeftimov.automechanic.car.models.CarModel;
import com.popeftimov.automechanic.car.services.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
