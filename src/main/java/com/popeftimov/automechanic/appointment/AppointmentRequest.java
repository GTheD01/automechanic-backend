package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class AppointmentRequest {
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private User user;
}
