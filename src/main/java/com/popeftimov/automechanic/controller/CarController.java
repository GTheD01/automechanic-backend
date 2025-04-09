package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.service.CarModelService;
import com.popeftimov.automechanic.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "Get list of car brands", description = "Fetch a paginated list of car brands for the admin panel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of car brands",
                    content = @Content(mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = CarBrandResponse.class))),
                                    @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                            })),
    })
    @GetMapping("/admin/brands")
    public ResponseEntity<Page<CarBrandResponse>> getAdminCarBrands(@ParameterObject Pageable pageable) {
        Page<CarBrandResponse> carBrandResponses = carBrandService.getAdminCarBrands(pageable);
        return ResponseEntity.ok(carBrandResponses);
    }

    @Operation(summary = "Create a new car brand", description = "Creates a new car brand if the brand name is not already taken.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the car brand",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarBrandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Brand name is missing or brand already exists",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/admin/brands")
    public ResponseEntity<CarBrandResponse> addCarBrand(@RequestBody CarBrandRequest carBrandRequest) {
        return carBrandService.createCarBrand(carBrandRequest.getBrandName());
    }

    @Operation(summary = "Delete a car brand", description = "Deletes a car brand by its name if it exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the car brand"),
            @ApiResponse(responseCode = "404", description = "Car brand not found",
                    content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("/admin/brands/{brandName}")
    public ResponseEntity<Void> deleteCarBrand(@PathVariable("brandName") String brandName) {
        carBrandService.deleteCarBrand(brandName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create a new car model", description = "Creates a new car model for an existing car brand if the model name doesn't already exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the car model",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarModelResponse.class))),
            @ApiResponse(responseCode = "400", description = "Car model already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Car brand not found",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/admin/{brandName}/create-model")
    public ResponseEntity<CarModelResponse> createCarBrandModel(
            @PathVariable("brandName") String brandName, @RequestBody CarModelRequest carModelRequest) {
        return carModelService.createCarBrandModel(brandName, carModelRequest.getModelName());
    }

    @Operation(summary = "Delete a car model", description = "Deletes a car model by its name if it exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the car model"),
            @ApiResponse(responseCode = "404", description = "Car model not found",
                    content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("/admin/car-models/{modelName}")
    public ResponseEntity<Void> deleteCarModel(@PathVariable("modelName") String modelName) {
        return carModelService.deleteCarModel(modelName);
    }

    @Operation(summary = "Get cars owned by a user", description = "Fetches a list of cars associated with a given user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the user's cars",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
    })
    @GetMapping("admin/cars/user/{userId}")
    public ResponseEntity<List<CarResponse>> getUserCars(@PathVariable("userId") Long userId) {
        return carService.getUserCars(userId);
    }

    @Operation(summary = "Get all car models", description = "Fetches a paginated list of all car models.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of car models",
                    content = @Content(mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = CarModelResponse.class))),
                                    @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                            })),
    })
    @GetMapping("/admin/car-models")
    public ResponseEntity<Page<CarModelResponse>> getCarModels(@ParameterObject Pageable pageable) {
        Page<CarModelResponse> carModelResponseList = carModelService.getAllCarModels(pageable);
        return ResponseEntity.ok(carModelResponseList);
    }

    @Operation(summary = "Get all car brands", description = "Fetches a list of all car brands.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of car brands",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarBrandResponse.class))),
    })
    @GetMapping("/brands")
    public List<CarBrandResponse> getCarBrands() {
        return carBrandService.getAllCarBrands();
    }

    @Operation(summary = "Get all car models by brand", description = "Fetches a list of all car models associated with a specific car brand.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of car models for the brand",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarModelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Car brand not found",
                    content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/{brand}/models")
    public List<CarModelResponse> getCarModelsByBrand(@PathVariable("brand") String brandName) {
        return carModelService.getAllCarModelsByBrand(brandName);
    }

    @Operation(summary = "Add a new car", description = "Adds a new car to the user's collection with brand, model, year, and version.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added the car",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or car details (e.g., invalid year, brand, model)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Car brand or model not found",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/cars")
    public ResponseEntity<CarResponse> addCar(@RequestBody CarRequest carRequest) {
        return carService.addCar(carRequest);
    }

    @Operation(summary = "Get all cars of the logged-in user", description = "Fetches a list of all cars associated with the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched the list of cars for the logged-in user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated",
                    content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> getLoggedInUserCars() {
        return carService.getLoggedInUserCars();
    }

    @Operation(summary = "Get details of a specific car", description = "Fetches the details of a specific car.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched car details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "403", description = "Permission denied",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarResponse> getCar(@PathVariable("carId") Long carId) {
        return carService.getCar(carId);
    }

    @Operation(summary = "Update a car's details", description = "Updates the details of a specific car including its brand, model, year, and version.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the car details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., invalid year)",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Permission denied for the current user",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Car, brand, or model not found",
                    content = @Content(mediaType = "application/json")),
    })
    @PutMapping("/cars/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable("carId") Long carId, @RequestBody CarRequest carRequest) {
        return carService.updateCar(carId, carRequest);
    }

    @Operation(summary = "Delete a car", description = "Deletes a specific car from the system if the user is admin or owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the car",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Permission denied for the current user",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Car not found",
                    content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable("carId") Long carId) {
        return carService.deleteCar(carId);
    }
}
