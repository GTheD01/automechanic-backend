package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.global.ErrorResponse;
import com.popeftimov.automechanic.user.User;
import com.popeftimov.automechanic.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<String> appointments() {
        return ResponseEntity.ok("Get appointments");
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        LocalDateTime requestedDateTime = LocalDateTime.of(appointmentRequest.getAppointmentDate(), appointmentRequest.getAppointmentTime());

        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid date/time: cannot schedule in the past"));
        }

        boolean appointmentExists = appointmentService.isAppointmentAtTimeExists(appointmentRequest.getAppointmentDate(), appointmentRequest.getAppointmentTime());
        if (appointmentExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Conflict: Appointment already exists at this date and time"));
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        AppointmentResponse appointment = appointmentService.createAppointment(appointmentRequest, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointment);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Appointment>> getAppointmentsForLoggedInUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Appointment> appointments = appointmentService.getAppointmentsByUser(user);
        return ResponseEntity.ok(appointments);
    }
}
