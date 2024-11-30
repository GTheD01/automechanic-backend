package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private User user;
}
