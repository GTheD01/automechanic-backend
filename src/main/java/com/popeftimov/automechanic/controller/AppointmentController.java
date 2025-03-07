package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.AppointmentUpdateRequest;
import com.popeftimov.automechanic.model.AppointmentStatus;
import com.popeftimov.automechanic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/admin/appointments")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            Pageable pageable,
            @RequestParam(value = "search", required = false) String search
    ) {
        Page<AppointmentResponse> appointmentPage = appointmentService.getAllApointments(pageable, search);
        return ResponseEntity.ok().body(appointmentPage);
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointment = appointmentService.createAppointment(appointmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointment);
    }

    @GetMapping("/appointments/me")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByLoggedInUser(Pageable pageable) {
        Page<AppointmentResponse> appointments = appointmentService.getAppointmentsByLoggedInUser(pageable);

        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/admin/appointments/{userId}")
    public ResponseEntity<Page<AppointmentResponse>> getUserAppointments(@PathVariable("userId") Long userId, Pageable pageable) {
        Page<AppointmentResponse> userAppointments = appointmentService.getUserAppointments(userId, pageable);
        return ResponseEntity.ok().body(userAppointments);
    }

    @PatchMapping("/admin/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                                 @RequestBody AppointmentUpdateRequest appointmentUpdateRequest) {
        AppointmentStatus appointmentStatus = appointmentUpdateRequest.getAppointmentStatus();
        return appointmentService.updateAppointment(appointmentId, appointmentStatus);
    }
}
