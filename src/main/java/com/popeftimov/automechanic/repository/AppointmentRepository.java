package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    Page<Appointment> findByUserOrderByAppointmentDateAscAppointmentTimeAsc(User user, Pageable pageable);
    boolean existsByAppointmentDateAndAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime);
    Optional<Appointment> findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();
    Optional<Appointment> findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(User user);
}
