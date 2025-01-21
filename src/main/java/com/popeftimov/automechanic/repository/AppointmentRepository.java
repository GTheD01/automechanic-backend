package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByUser(User user);
    boolean existsByAppointmentDateAndAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime);
    Optional<Appointment> findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();
}
