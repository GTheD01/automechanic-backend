package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private CarBrandResponse carBrand;
    private CarModelResponse model;
    private Integer year;
    private String version;
    private List<AppointmentResponse> appointments;
}
