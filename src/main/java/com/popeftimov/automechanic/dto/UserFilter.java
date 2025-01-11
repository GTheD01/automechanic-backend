package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
    private String name;
    private Boolean hasCars;
    private Boolean hasAppointments;
}
