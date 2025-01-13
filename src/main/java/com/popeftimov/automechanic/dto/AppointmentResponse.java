package com.popeftimov.automechanic.dto;

import com.popeftimov.automechanic.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus appointmentStatus;
    private UserResponse user;
}
