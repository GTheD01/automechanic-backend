package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarBrandResponse;
import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.dto.CarRequest;
import com.popeftimov.automechanic.dto.CarResponse;
import com.popeftimov.automechanic.exception.CarExceptions;
import com.popeftimov.automechanic.model.Car;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final UserRepository userRepository;
    private final CarBrandService carBrandService;
    private final CarModelService carModelService;

    public CarResponse convertCarToCarResponse(Car car) {
        CarBrandResponse carBrandResponse = carBrandService.convertCarBrandToCarBrandResponse(car.getBrand());
        CarModelResponse carModelResponse = carModelService.convertCarModelToCarModelResponse(car.getModel());
        return new CarResponse(
                car.getId(),
                carBrandResponse,
                carModelResponse,
                car.getYear(),
                car.getVersion()
        );
    }

    public ResponseEntity<?> addCar(@RequestBody CarRequest carRequest) {
        String brandName = carRequest.getBrandName();
        String modelName = carRequest.getModelName();
        Integer year = carRequest.getYear();
        String version = carRequest.getVersion();

        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            throw new CarExceptions.CarBrandNotFound();
        }
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            throw new CarExceptions.CarModelNotFound();
        }
        if (year == null || (1950 > year || year > 2025)) {
            throw new CarExceptions.CarYearInvalid();
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Car car = new Car();
        car.setBrand(carBrand);
        car.setModel(carModel);
        car.setUser(user);
        car.setYear(year);
        car.setVersion(version);
        carRepository.save(car);
        CarResponse carResponse = this.convertCarToCarResponse(car);
        return ResponseEntity.ok(carResponse);
    }

    public ResponseEntity<?> getUserCars(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID: " + userId + " not found"));
        List<Car> userCars = user.getCars();
        List<CarResponse> userCarsResponse = userCars.stream().map(this::convertCarToCarResponse).toList();

        return ResponseEntity.ok(userCarsResponse);
    }

    public ResponseEntity<?> getLoggedInUserCars() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Car> loggedInUserCars = carRepository.findByUser(user);
        List<CarResponse> userCarsResponse = loggedInUserCars.stream().map(this::convertCarToCarResponse).toList();

        return ResponseEntity.ok(userCarsResponse);
    }
}
