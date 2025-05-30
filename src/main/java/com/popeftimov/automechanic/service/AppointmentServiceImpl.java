package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.exception.appointment.AppointmentExceptions;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.AppointmentStatus;
import com.popeftimov.automechanic.model.Car;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.specifications.AppointmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final CarService carService;

    @Override
    public boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime) {
        return appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime);
    }

    @Override
    public AppointmentResponse updateAppointment(Long appointmentId, AppointmentUpdateRequest appointmentUpdateRequest) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new AppointmentExceptions.AppointmentNotFoundException(appointmentId)
        );

        AppointmentStatus appointmentStatus = appointmentUpdateRequest.getAppointmentStatus();

        appointment.setAppointmentStatus(appointmentStatus);
        appointmentRepository.save(appointment);
        return convertToAppointmentResponse(appointment);
    }

    @Override
    public Page<AppointmentResponse> getUserAppointments(Long userId, Pageable pageable) {
        User user = userService.loadUser
(userId);
        Page<Appointment> userAppointments = appointmentRepository.findByUserOrderByAppointmentDateAscAppointmentTimeAsc(user, pageable);
        return userAppointments
                .map(this::convertToAppointmentResponse);
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest appointment) {
        LocalDateTime requestedDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());

        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new AppointmentExceptions.AppointmentCannotScheduleInThePastException();
        }

        boolean appointmentExists = isAppointmentAtTimeExists(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        if (appointmentExists) {
            throw new AppointmentExceptions.AppointmentAtDateTimeExistsException();
        }

        if (appointment.getCarId() == null) {
            throw new AppointmentExceptions.AppointmentNoCarSelectedException();
        }

        Car userCar = carService.findCar(appointment.getCarId());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUser(email);

        Appointment newAppointment = new Appointment();
        newAppointment.setUser(user);
        newAppointment.setAppointmentDate(appointment.getAppointmentDate());
        newAppointment.setAppointmentTime(appointment.getAppointmentTime());
        newAppointment.setDescription(appointment.getDescription());
        newAppointment.setAppointmentStatus(AppointmentStatus.UPCOMING);
        newAppointment.setCar(userCar);

        appointmentRepository.save(newAppointment);

        return this.convertToAppointmentResponse(newAppointment);
    }

    public AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        UserResponse userResponse = UserResponse
                .builder()
                .firstName(appointment.getUser().getFirstName())
                .lastName(appointment.getUser().getLastName())
                .phoneNumber(appointment.getUser().getPhoneNumber())
                .build();

        CarResponse carResponse = carService.convertCarToCarResponse(appointment.getCar());

        return AppointmentResponse
                .builder()
                .id(appointment.getId())
                .description(appointment.getDescription())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .user(userResponse)
                .car(carResponse)
                .createdAt(appointment.getCreatedAt())
                .lastModifiedAt(appointment.getLastModifiedAt())
                .build();
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByLoggedInUser(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Appointment> userAppointments = appointmentRepository.findByUserOrderByAppointmentDateAscAppointmentTimeAsc(user, pageable);

        return userAppointments
                .map(this::convertToAppointmentResponse);
    }

    @Override
    public Page<AppointmentResponse> getAllApointments(Pageable pageable, String search) {
        AppointmentFilter filter = new AppointmentFilter(search);
        Specification<Appointment> spec = AppointmentSpecification.applyFilters(filter);

        Page<Appointment> appointments = appointmentRepository.findAll(spec, pageable);

        return appointments
                .map(this::convertToAppointmentResponse);
    }
}
