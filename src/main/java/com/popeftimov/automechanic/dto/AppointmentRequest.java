package com.popeftimov.automechanic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.popeftimov.automechanic.model.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    private User user;
}
