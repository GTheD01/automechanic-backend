package com.popeftimov.automechanic.dto;

import com.popeftimov.automechanic.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequest {
    private AppointmentStatus appointmentStatus;
}
