package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> getAppointmentsByUser();
    AppointmentResponse createAppointment(AppointmentRequest appointment);
    boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime);
}
