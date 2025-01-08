package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.AppointmentExceptions;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime) {
        return appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime);
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest appointment) {
        LocalDateTime requestedDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());

        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new AppointmentExceptions.AppointmentCannotScheduleInThePast();
        }

        boolean appointmentExists = isAppointmentAtTimeExists(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        if (appointmentExists) {
            throw new AppointmentExceptions.AppointmentAtDateTimeExists();
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        Appointment newAppointment = new Appointment();
        newAppointment.setUser(user);
        newAppointment.setAppointmentDate(appointment.getAppointmentDate());
        newAppointment.setAppointmentTime(appointment.getAppointmentTime());
        newAppointment.setDescription(appointment.getDescription());

        appointmentRepository.save(newAppointment);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserRole(),
                user.getAvatar()
        );

        return new AppointmentResponse(newAppointment.getId(), newAppointment.getDescription(), newAppointment.getAppointmentDate(),
                newAppointment.getAppointmentTime(), userResponse);
    }

    @Override
    public List<Appointment> getAppointmentsByUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return appointmentRepository.findByUser(user);
    }
}
