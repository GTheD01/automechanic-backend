package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.exception.CarExceptions;
import com.popeftimov.automechanic.model.*;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.utils.PermissionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return CarResponse
                .builder()
                .id(car.getId())
                .carBrand(carBrandResponse)
                .model(carModelResponse)
                .year(car.getYear())
                .version(car.getVersion())
                .build();
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

    public ResponseEntity<?> updateCar(Long carId, CarRequest carRequest) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFound(carId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
        }

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

        car.setBrand(carBrand);
        car.setModel(carModel);
        car.setYear(year);
        car.setVersion(version);
        carRepository.save(car);
        CarResponse carResponse = this.convertCarToCarResponse(car);
        return ResponseEntity.ok(carResponse);
    }

    public ResponseEntity<?> deleteCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFound(carId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
        }

        carRepository.delete(car);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> getCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFound(carId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
        }

        CarBrandResponse carBrandResponse = carBrandService.convertCarBrandToCarBrandResponse(car.getBrand());
        CarModelResponse carModelResponse = carModelService.convertCarModelToCarModelResponse(car.getModel());
        List<AppointmentResponse> appointmentResponses = car
                .getAppointments()
                .stream()
                .map((appointment -> AppointmentResponse
                        .builder()
                        .id(appointment.getId())
                        .description(appointment.getDescription())
                        .appointmentDate(appointment.getAppointmentDate())
                        .appointmentTime(appointment.getAppointmentTime())
                        .appointmentStatus(appointment.getAppointmentStatus())
                        .createdDate(appointment.getCreatedDate())
                        .lastModifiedDate(appointment.getLastModifiedDate())
                        .build()
                ))
                .toList();

        CarResponse carResponse = CarResponse
                .builder()
                .id(car.getId())
                .carBrand(carBrandResponse)
                .model(carModelResponse)
                .year(car.getYear())
                .version(car.getVersion())
                .appointments(appointmentResponses)
                .build();

        return ResponseEntity.ok(carResponse);
    }
}
