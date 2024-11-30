package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.user.User;
import com.popeftimov.automechanic.user.UserResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private UserResponse user;

    public AppointmentResponse(Long id, String description, LocalDate appointmentDate, LocalTime appointmentTime, UserResponse user) {
        this.id = id;
        this.description = description;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.user = user;
    }
}
