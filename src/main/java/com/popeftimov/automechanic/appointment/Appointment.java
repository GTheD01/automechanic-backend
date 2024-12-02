package com.popeftimov.automechanic.appointment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.popeftimov.automechanic.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

}
