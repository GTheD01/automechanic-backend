package com.popeftimov.automechanic.appointment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    @GetMapping
    public ResponseEntity<String> appointments() {
        return ResponseEntity.ok("Get appointments");
    }
}
