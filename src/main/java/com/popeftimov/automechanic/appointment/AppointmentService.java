package com.popeftimov.automechanic.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAppointmentsByUser();
    AppointmentResponse createAppointment(AppointmentRequest appointment);
    boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime);
}
