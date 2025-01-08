package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private UserResponse user;
}