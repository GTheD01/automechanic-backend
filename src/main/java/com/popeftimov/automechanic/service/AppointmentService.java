package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAppointmentsByUser();
    AppointmentResponse createAppointment(AppointmentRequest appointment);
    boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime);
}
