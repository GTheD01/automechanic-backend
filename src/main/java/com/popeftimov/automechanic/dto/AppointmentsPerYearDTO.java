package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentsPerYearDTO {
    private Integer year;
    private Long appointments;

}
