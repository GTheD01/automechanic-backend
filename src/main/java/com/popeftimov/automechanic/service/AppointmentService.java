package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentService {
    Page<AppointmentResponse> getAppointmentsByLoggedInUser(Pageable pageable);
    Page<AppointmentResponse> getAllApointments(Pageable pageable, String search);
    AppointmentResponse createAppointment(AppointmentRequest appointment);
    boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime);
    AppointmentResponse convertToAppointmentResponse(Appointment appointment);
    ResponseEntity<AppointmentResponse> updateAppointment(Long appointmentId, AppointmentStatus appointmentStatus);
    Page<AppointmentResponse> getUserAppointments(Long userId, Pageable pageable);
}
