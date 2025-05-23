package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.exception.car.CarExceptions;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.Car;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import com.popeftimov.automechanic.utils.PermissionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final CarBrandService carBrandService;
    private final CarModelService carModelService;
    private final UserService userService;

    public Car findCar(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFoundException(carId));
    }

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

    public ResponseEntity<CarResponse> addCar(@RequestBody CarRequest carRequest) {
        String brandName = carRequest.getBrandName();
        String modelName = carRequest.getModelName();
        Integer year = carRequest.getYear();
        String version = carRequest.getVersion();

        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            throw new CarExceptions.CarBrandNotFoundException();
        }
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            throw new CarExceptions.CarModelNotFoundException();
        }
        if (year == null || (1950 > year || year > 2025)) {
            throw new CarExceptions.CarYearInvalidException();
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
        return ResponseEntity.status(HttpStatus.CREATED).body(carResponse);
    }

    public ResponseEntity<List<CarResponse>> getUserCars(Long userId) {
        User user = userService.loadUser(userId);
        List<Car> userCars = user.getCars();
        List<CarResponse> userCarsResponse = userCars.stream().map(this::convertCarToCarResponse).toList();

        return ResponseEntity.ok(userCarsResponse);
    }

    public ResponseEntity<List<CarResponse>> getLoggedInUserCars() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Car> loggedInUserCars = carRepository.findByUser(user);
        List<CarResponse> userCarsResponse = loggedInUserCars.stream().map(this::convertCarToCarResponse).toList();

        return ResponseEntity.ok(userCarsResponse);
    }

    public ResponseEntity<CarResponse> updateCar(Long carId, CarRequest carRequest) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFoundException(carId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUser(email);

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            throw new UserExceptions.PermissionDeniedException();
        }

        String brandName = carRequest.getBrandName();
        String modelName = carRequest.getModelName();
        Integer year = carRequest.getYear();
        String version = carRequest.getVersion();

        CarBrand carBrand = carBrandRepository.findByName(brandName);
        if (carBrand == null) {
            throw new CarExceptions.CarBrandNotFoundException();
        }
        CarModel carModel = carModelRepository.findByName(modelName);
        if (carModel == null) {
            throw new CarExceptions.CarModelNotFoundException();
        }
        if (year == null || (1950 > year || year > 2025)) {
            throw new CarExceptions.CarYearInvalidException();
        }

        car.setBrand(carBrand);
        car.setModel(carModel);
        car.setYear(year);
        car.setVersion(version);
        carRepository.save(car);
        CarResponse carResponse = this.convertCarToCarResponse(car);
        return ResponseEntity.ok(carResponse);
    }

    public ResponseEntity<Void> deleteCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFoundException(carId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUser(email);

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            throw new UserExceptions.PermissionDeniedException();
        }

        carRepository.delete(car);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<CarResponse> getCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarExceptions.CarNotFoundException(carId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUser(email);

        if (PermissionUtils.notOwnerOrAdmin(user, car)) {
            throw new UserExceptions.PermissionDeniedException();
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
                        .createdAt(appointment.getCreatedAt())
                        .lastModifiedAt(appointment.getLastModifiedAt())
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
