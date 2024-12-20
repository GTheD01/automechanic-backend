package com.popeftimov.automechanic.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointment = appointmentService.createAppointment(appointmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointment);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Appointment>> getAppointmentsForLoggedInUser() {
        List<Appointment> appointments = appointmentService.getAppointmentsByUser();

        return ResponseEntity.ok(appointments);
    }
}
