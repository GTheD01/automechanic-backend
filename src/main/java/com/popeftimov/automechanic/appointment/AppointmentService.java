package com.popeftimov.automechanic.appointment;

import com.popeftimov.automechanic.user.User;
import com.popeftimov.automechanic.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime) {
        return appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime);
    }

    public AppointmentResponse createAppointment(AppointmentRequest appointment, User user) {
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
                user.getRole()
        );

        return new AppointmentResponse(newAppointment.getId(), newAppointment.getDescription(), newAppointment.getAppointmentDate(),
                newAppointment.getAppointmentTime(), userResponse);
    }

    public List<Appointment> getAppointmentsByUser(User user) {
        return appointmentRepository.findByUser(user);
    }
}
