package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarBrandResponse;
import com.popeftimov.automechanic.exception.car.CarExceptions;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CarBrandServiceTest {

    @Mock
    private CarBrandRepository carBrandRepository;

    @InjectMocks
    private CarBrandService carBrandService;

    @Test
    void convertCarBrandToCarBrandResponse_shouldReturnCorrectResponse() {
        CarBrand carBrand = new CarBrand("Toyota");
        carBrand.setId(1L);

        CarBrandResponse response = carBrandService.convertCarBrandToCarBrandResponse(carBrand);

        assertEquals(carBrand.getId(), response.getId());
        assertEquals(carBrand.getName(), response.getName());
    }

    @Test
    void getAdminCarBrands_shouldReturnPagedResponse() {
        CarBrand carBrand = new CarBrand("BMW");
        carBrand.setId(2L);
        Page<CarBrand> carBrandPage = new PageImpl<>(List.of(carBrand));
        when(carBrandRepository.findAll(any(Pageable.class))).thenReturn(carBrandPage);

        Page<CarBrandResponse> result = carBrandService.getAdminCarBrands(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(carBrand.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getAllCarBrands_shouldReturnListOfCarBrandResponses() {
        CarBrand bmw = new CarBrand("BMW");
        bmw.setId(1L);
        CarBrand audi = new CarBrand("Audi");
        audi.setId(2L);

        when(carBrandRepository.findAll()).thenReturn(List.of(bmw, audi));

        List<CarBrandResponse> result = carBrandService.getAllCarBrands();

        assertEquals(2, result.size());
        assertEquals(bmw.getName(), result.get(0).getName());
    }

    @Test
    void createCarBrand_shouldSaveNewBrandAndReturnResponse() {
        String brandName = "Mazda";
        when(carBrandRepository.findByName(brandName)).thenReturn(null); // No existing brand

        ResponseEntity<CarBrandResponse> response = carBrandService.createCarBrand(brandName);

        assertEquals(201, response.getStatusCode().value(), "Expected status code 201 (CREATED)");

        CarBrandResponse responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
        assertEquals(brandName, responseBody.getName(), "The brand name in the response should match the input");

        ArgumentCaptor<CarBrand> brandCaptor = ArgumentCaptor.forClass(CarBrand.class);
        verify(carBrandRepository).save(brandCaptor.capture());
        assertNotNull(brandCaptor.getValue(), "The CarBrand object should be saved");
        assertEquals(brandName, brandCaptor.getValue().getName(), "The name of the saved car brand should match the input");
    }

    @Test
    void createCarBrand_shouldThrowWhenNameIsNull() {
        assertThrows(CarExceptions.CarBrandNotProvidedException.class, () -> {
            carBrandService.createCarBrand(null);
        }, "Expected CarBrandNotProvidedException when brand name is null");
    }

    @Test
    void createCarBrand_shouldThrowWhenBrandExists() {
        String existing = "Toyota";
        when(carBrandRepository.findByName(existing)).thenReturn(new CarBrand(existing));

        assertThrows(CarExceptions.CarBrandExistsException.class, () -> {
            carBrandService.createCarBrand(existing);
        }, "Expected CarBrandExistsException when brand already exists");
    }

    @Test
    void deleteCarBrand_shouldDeleteWhenFound() {
        String brand = "Ford";
        CarBrand foundBrand = new CarBrand(brand);
        when(carBrandRepository.findByName(brand)).thenReturn(foundBrand);

        carBrandService.deleteCarBrand(brand);

        verify(carBrandRepository).delete(foundBrand);
    }

    @Test
    void deleteCarBrand_shouldThrowWhenNotFound() {
        when(carBrandRepository.findByName("Lada")).thenReturn(null);

        assertThrows(CarExceptions.CarBrandNotFoundException.class, () -> {
            carBrandService.deleteCarBrand("Lada");
        });
    }
}