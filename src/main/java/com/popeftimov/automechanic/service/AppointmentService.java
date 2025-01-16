package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> getAppointmentsByUser();
    Page<AppointmentResponse> getAllApointments(Pageable pageable, String search);
    AppointmentResponse createAppointment(AppointmentRequest appointment);
    boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime);

    ResponseEntity<AppointmentResponse> updateAppointment(Long appointmentId, AppointmentStatus appointmentStatus);
}
