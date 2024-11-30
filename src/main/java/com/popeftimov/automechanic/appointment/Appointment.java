package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue
    private String id;
    private String description;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Appointment(String id, String description, LocalDate appointmentDate, LocalTime appointmentTime) {
        this.id = id;
        this.description = description;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

}
