package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.AppointmentExceptions;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.AppointmentStatus;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

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
        newAppointment.setAppointmentStatus(AppointmentStatus.UPCOMING);

        appointmentRepository.save(newAppointment);

        UserResponse userResponse = userService.convertToUserResponse(user);

        return new AppointmentResponse(newAppointment.getId(), newAppointment.getDescription(),
                newAppointment.getAppointmentDate(), newAppointment.getAppointmentTime(),
                AppointmentStatus.UPCOMING, userResponse, newAppointment.getCreatedDate(),
                newAppointment.getLastModifiedDate());
    }

    public List<AppointmentResponse> convertToAppointmentDtoList(List<Appointment> appointments) {
        List<AppointmentResponse> appointmentResponseList = new ArrayList<>();

        for (Appointment appointment : appointments) {
            AppointmentResponse appointmentResponse = AppointmentResponse.builder()
                    .id(appointment.getId())
                    .description(appointment.getDescription())
                    .appointmentDate(appointment.getAppointmentDate())
                    .appointmentTime(appointment.getAppointmentTime())
                    .user(userService.convertToUserResponse(appointment.getUser()))
                    .build();

            appointmentResponseList.add(appointmentResponse);
        }

        return appointmentResponseList;
    }


    @Override
    public List<AppointmentResponse> getAppointmentsByUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Appointment> userAppointments = appointmentRepository.findByUser(user);

        return convertToAppointmentDtoList(userAppointments);
    }

    @Override
    public Page<AppointmentResponse> getAllApointments(Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        Page<AppointmentResponse> appointmentResponses = appointments
                .map(appointment -> {
                    UserResponse userResponse = userService.convertToUserResponse(appointment.getUser());
                    return new AppointmentResponse(
                            appointment.getId(),
                            appointment.getDescription(),
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getAppointmentStatus(),
                            userResponse,
                            appointment.getCreatedDate(),
                            appointment.getLastModifiedDate()
                    );
                });
        return appointmentResponses;
    }
}
