package com.popeftimov.automechanic.car.dto;

import com.popeftimov.automechanic.car.models.CarModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarModelResponse {
    private Long id;
    private String name;

    public static CarModelResponse from(CarModel carModel) {
        return new CarModelResponse(carModel.getId(), carModel.getName());
    }
}
