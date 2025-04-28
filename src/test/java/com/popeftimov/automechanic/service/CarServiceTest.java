package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarBrandResponse;
import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.dto.CarRequest;
import com.popeftimov.automechanic.dto.CarResponse;
import com.popeftimov.automechanic.exception.car.CarExceptions;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.*;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarBrandRepository carBrandRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CarBrandService carBrandService;

    @Mock
    private CarModelService carModelService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CarService carService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testFindCar_Found_ReturnsCar() {
        Car car = new Car();
        car.setId(1L);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Car foundCar = carService.findCar(1L);

        assertEquals(1L, foundCar.getId());
    }

    @Test
    void testFindCar_NotFound_ThrowsException() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CarExceptions.CarNotFoundException.class, () -> carService.findCar(1L));
    }

    @Test
    void testConvertCarToCarResponse_ValidCar_ReturnsCarResponse() {
        CarBrand brand = new CarBrand();
        CarModel model = new CarModel();

        Car car = new Car();
        car.setId(1L);
        car.setBrand(brand);
        car.setModel(model);
        car.setYear(2021);
        car.setVersion("Sport");

        CarBrandResponse brandResponse = new CarBrandResponse();
        CarModelResponse modelResponse = new CarModelResponse();

        when(carBrandService.convertCarBrandToCarBrandResponse(brand)).thenReturn(brandResponse);
        when(carModelService.convertCarModelToCarModelResponse(model)).thenReturn(modelResponse);

        CarResponse response = carService.convertCarToCarResponse(car);

        assertEquals(1L, response.getId());
        assertEquals(2021, response.getYear());
        assertEquals("Sport", response.getVersion());
        assertEquals(brandResponse, response.getCarBrand());
        assertEquals(modelResponse, response.getModel());
    }

    @Test
    void testAddCar_Success() {
        CarRequest request = new CarRequest("Toyota", "Corolla", 2020, "LE");

        CarBrand carBrand = new CarBrand();
        CarModel carModel = new CarModel();
        User user = new User();
        Car car = new Car();
        car.setBrand(carBrand);
        car.setModel(carModel);
        car.setUser(user);
        car.setYear(2020);
        car.setVersion("LE");

        when(carBrandRepository.findByName("Toyota")).thenReturn(carBrand);
        when(carModelRepository.findByName("Corolla")).thenReturn(carModel);
        when(authentication.getPrincipal()).thenReturn(user);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(carBrandService.convertCarBrandToCarBrandResponse(carBrand)).thenReturn(new CarBrandResponse());
        when(carModelService.convertCarModelToCarModelResponse(carModel)).thenReturn(new CarModelResponse());

        ResponseEntity<CarResponse> response = carService.addCar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void testAddCar_ThrowsCarBrandNotFoundException() {
        CarRequest request = new CarRequest("UnknownBrand", "Corolla", 2020, "LE");

        when(carBrandRepository.findByName("UnknownBrand")).thenReturn(null);

        assertThrows(CarExceptions.CarBrandNotFoundException.class, () -> carService.addCar(request));
    }

    @Test
    void testAddCar_ThrowsCarModelNotFoundException() {
        CarRequest request = new CarRequest("Toyota", "UnknownModel", 2020, "LE");

        when(carBrandRepository.findByName("Toyota")).thenReturn(new CarBrand());
        when(carModelRepository.findByName("UnknownModel")).thenReturn(null);

        assertThrows(CarExceptions.CarModelNotFoundException.class, () -> carService.addCar(request));
    }

    @Test
    void testAddCar_ThrowsCarYearInvalidException_WhenYearInvalid() {
        CarRequest requestLowYear = new CarRequest("Toyota", "Corolla", 1949, "Version");
        CarRequest requestHighYear = new CarRequest("Toyota", "Corolla", 2026, "Version");
        CarRequest requestNullYear = new CarRequest("Toyota", "Corolla", null, "Version");

        when(carBrandRepository.findByName("Toyota")).thenReturn(new CarBrand());
        when(carModelRepository.findByName("Corolla")).thenReturn(new CarModel());

        assertThrows(CarExceptions.CarYearInvalidException.class, () -> carService.addCar(requestLowYear));
        assertThrows(CarExceptions.CarYearInvalidException.class, () -> carService.addCar(requestHighYear));
        assertThrows(CarExceptions.CarYearInvalidException.class, () -> carService.addCar(requestNullYear));
    }

    @Test
    void testGetUserCars_ReturnsCarResponses() {
        User user = new User();
        Car car = new Car();
        car.setBrand(new CarBrand());
        car.setModel(new CarModel());
        user.setCars(List.of(car));

        when(userService.loadUser(1L)).thenReturn(user);
        when(carBrandService.convertCarBrandToCarBrandResponse(any())).thenReturn(new CarBrandResponse());
        when(carModelService.convertCarModelToCarModelResponse(any())).thenReturn(new CarModelResponse());

        ResponseEntity<List<CarResponse>> response = carService.getUserCars(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetLoggedInUserCars_ReturnsCarResponses() {
        User user = new User();
        Car car = new Car();
        car.setBrand(new CarBrand());
        car.setModel(new CarModel());
        List<Car> cars = List.of(car);

        when(authentication.getPrincipal()).thenReturn(user);
        when(carRepository.findByUser(user)).thenReturn(cars);
        when(carBrandService.convertCarBrandToCarBrandResponse(any())).thenReturn(new CarBrandResponse());
        when(carModelService.convertCarModelToCarModelResponse(any())).thenReturn(new CarModelResponse());

        ResponseEntity<List<CarResponse>> response = carService.getLoggedInUserCars();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testUpdateCar_ThrowsCarNotFoundException() {
        Long carId = 1L;
        CarRequest request = new CarRequest("Toyota", "Corolla", 2020, "Version");

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(CarExceptions.CarNotFoundException.class, () -> carService.updateCar(carId, request));
    }

    @Test
    void testUpdateCar_PermissionDenied() {
        Car car = new Car();
        car.setId(1L);
        car.setUser(new User());

        CarRequest request = new CarRequest("BMW", "M3", 2022, "Competition");
        User otherUser = new User();
        otherUser.setId(99L);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.loadUser("user@example.com")).thenReturn(otherUser);

        assertThrows(UserExceptions.PermissionDeniedException.class, () -> carService.updateCar(1L, request));
    }

    @Test
    void testUpdateCar_ThrowsCarBrandNotFoundException() {
        Long carId = 1L;
        CarRequest request = new CarRequest("UnknownBrand", "Corolla", 2020, "Version");

        Car car = new Car();
        car.setId(carId);
        car.setUser(new User());

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));  // Ensure car is found
        when(carBrandRepository.findByName("UnknownBrand")).thenReturn(null);  // Brand not found

        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        when(userService.loadUser("user@example.com")).thenReturn(user);  // Mock user

        assertThrows(CarExceptions.CarBrandNotFoundException.class, () -> carService.updateCar(carId, request));
    }

    @Test
    void testUpdateCar_ThrowsCarModelNotFoundException() {
        Long carId = 1L;
        CarRequest request = new CarRequest("Toyota", "UnknownModel", 2020, "Version");

        Car car = new Car();
        car.setId(carId);
        car.setUser(new User());

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));  // Ensure car is found
        when(carBrandRepository.findByName("Toyota")).thenReturn(new CarBrand());  // Brand found
        when(carModelRepository.findByName("UnknownModel")).thenReturn(null);  // Model not found

        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        when(userService.loadUser("user@example.com")).thenReturn(user);  // Mock user

        assertThrows(CarExceptions.CarModelNotFoundException.class, () -> carService.updateCar(carId, request));
    }


    @Test
    void testDeleteCar_Success() {
        Car car = new Car();
        car.setId(1L);
        User user = new User();
        car.setUser(user);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(authentication.getName()).thenReturn("email");
        when(userService.loadUser("email")).thenReturn(user);

        ResponseEntity<Void> response = carService.deleteCar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carRepository).delete(car);
    }

    @Test
    void testGetCar_Success() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand(new CarBrand());
        car.setModel(new CarModel());
        car.setUser(new User());
        car.setAppointments(List.of());

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(authentication.getName()).thenReturn("email");
        when(userService.loadUser("email")).thenReturn(car.getUser());
        when(carBrandService.convertCarBrandToCarBrandResponse(any())).thenReturn(new CarBrandResponse());
        when(carModelService.convertCarModelToCarModelResponse(any())).thenReturn(new CarModelResponse());

        ResponseEntity<CarResponse> response = carService.getCar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }
}
