package com.popeftimov.automechanic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.popeftimov.automechanic.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate appointmentDate;

    private LocalTime appointmentTime;
    private AppointmentStatus appointmentStatus;
    private UserResponse user;

    @JsonFormat(pattern = "HH:mm / dd.MM.yyyy")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "HH:mm / dd.MM.yyyy")
    private LocalDateTime lastModifiedDate;
}
