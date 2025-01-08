package com.popeftimov.automechanic.dto;

import com.popeftimov.automechanic.model.CarBrand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarBrandResponse {
    private Long id;
    private String name;

    public static CarBrandResponse from(CarBrand carBrand) {
        return new CarBrandResponse(carBrand.getId(), carBrand.getName());
    }
}
