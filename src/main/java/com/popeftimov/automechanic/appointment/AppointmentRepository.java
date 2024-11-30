package com.popeftimov.automechanic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    boolean existsByAppointmentDateAndAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime);
}
