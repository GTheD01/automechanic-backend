package com.popeftimov.automechanic.car.services;

import com.popeftimov.automechanic.car.models.Car;
import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.models.CarModel;
import com.popeftimov.automechanic.car.repository.CarBrandRepository;
import com.popeftimov.automechanic.car.repository.CarModelRepository;
import com.popeftimov.automechanic.car.repository.CarRepository;
import com.popeftimov.automechanic.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;

    public ResponseEntity<?> addCar(String brandName, String modelName, Integer year) {
        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car brand not found");
        }
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car model not found");
        }
        if(!carModel.getYears().contains(year)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model year not found");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Car car = new Car();
        car.setBrand(carBrand);
        car.setModel(carModel);
        car.setUser(user);
//        car.setYear(year); TODO: Add user field for the car year
        car = carRepository.save(car);
        return ResponseEntity.ok(car);
    }
}
