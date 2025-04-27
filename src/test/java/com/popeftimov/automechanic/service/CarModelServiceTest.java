package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.CarModelResponse;
import com.popeftimov.automechanic.exception.car.CarExceptions;
import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import com.popeftimov.automechanic.repository.CarBrandRepository;
import com.popeftimov.automechanic.repository.CarModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarModelServiceTest {

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CarBrandRepository carBrandRepository;

    @InjectMocks
    private CarModelService carModelService;

    @Test
    void testConvertCarModelToCarModelResponse() {
        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setName("test");

        CarModelResponse carModelResponse = carModelService.convertCarModelToCarModelResponse(carModel);

        assertEquals(carModel.getId(), carModelResponse.getId());
        assertEquals(carModel.getName(), carModelResponse.getName());
    }

    @Test
    void testCreateCarBrandModel_Success() {
        String brandName = "Toyota";
        String modelName = "Corolla";
        CarBrand brand = new CarBrand();
        brand.setName(brandName);

        when(carBrandRepository.findByName(brandName)).thenReturn(brand);
        when(carModelRepository.findByName(modelName)).thenReturn(null);

        ResponseEntity<CarModelResponse> response = carModelService.createCarBrandModel(brandName, modelName);

        ArgumentCaptor<CarModel> captor = ArgumentCaptor.forClass(CarModel.class);
        verify(carModelRepository).save(captor.capture());

        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(modelName, response.getBody().getName());
        assertEquals(modelName, captor.getValue().getName());
        assertEquals(brand, captor.getValue().getBrand());
    }

    @Test
    void testCreateCarBrandModel_BrandNotFound() {
        when(carBrandRepository.findByName("BMW")).thenReturn(null);

        assertThrows(CarExceptions.CarBrandNotFoundException.class,
                () -> carModelService.createCarBrandModel("BMW", "X5"));
    }

    @Test
    void testCreateCarBrandModel_ModelAlreadyExists() {
        CarBrand brand = new CarBrand();
        brand.setName("Toyota");

        CarModel existingModel = new CarModel("Corolla", brand);

        when(carBrandRepository.findByName("Toyota")).thenReturn(brand);
        when(carModelRepository.findByName("Corolla")).thenReturn(existingModel);

        assertThrows(CarExceptions.CarModelExistsException.class,
                () -> carModelService.createCarBrandModel("Toyota", "Corolla"));
    }

    @Test
    void testGetAllCarModelsByBrand_Success() {
        CarBrand brand = new CarBrand();
        brand.setName("Honda");

        CarModel model1 = new CarModel("Civic", brand);
        CarModel model2 = new CarModel("Accord", brand);

        when(carBrandRepository.findByName("Honda")).thenReturn(brand);
        when(carModelRepository.findByBrand(brand)).thenReturn(List.of(model1, model2));

        List<CarModelResponse> responses = carModelService.getAllCarModelsByBrand("Honda");

        assertEquals(2, responses.size());
        assertEquals("Civic", responses.get(0).getName());
        assertEquals("Accord", responses.get(1).getName());
    }

    @Test
    void testGetAllCarModelsByBrand_BrandNotFound() {
        when(carBrandRepository.findByName("Ford")).thenReturn(null);
        assertThrows(CarExceptions.CarBrandNotFoundException.class,
                () -> carModelService.getAllCarModelsByBrand("Ford"));
    }

    @Test
    void testGetAllCarModels_Pagination() {
        CarBrand brand = new CarBrand();
        CarModel model1 = new CarModel("Focus", brand);
        CarModel model2 = new CarModel("Fiesta", brand);

        Page<CarModel> page = new PageImpl<>(List.of(model1, model2));
        when(carModelRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CarModelResponse> result = carModelService.getAllCarModels(PageRequest.of(0, 10));
        assertEquals(2, result.getContent().size());
        assertEquals("Focus", result.getContent().get(0).getName());
    }

    @Test
    void testDeleteCarModel_Success() {
        CarModel model = new CarModel();
        model.setName("Astra");

        when(carModelRepository.findByName("Astra")).thenReturn(model);

        ResponseEntity<Void> response = carModelService.deleteCarModel("Astra");

        verify(carModelRepository).delete(model);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteCarModel_NotFound() {
        when(carModelRepository.findByName("Unknown")).thenReturn(null);
        assertThrows(CarExceptions.CarModelNotFoundException.class,
                () -> carModelService.deleteCarModel("Unknown"));
    }
}