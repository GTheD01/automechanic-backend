package com.popeftimov.automechanic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.popeftimov.automechanic.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class AppointmentRequest {
    private String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate appointmentDate;

    private LocalTime appointmentTime;
    private User user;
}
