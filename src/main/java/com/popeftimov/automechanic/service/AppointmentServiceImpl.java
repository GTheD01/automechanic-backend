package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentFilter;
import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.AppointmentExceptions;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.AppointmentStatus;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.specifications.AppointmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;

    @Override
    public boolean isAppointmentAtTimeExists(LocalDate appointmentDate, LocalTime appointmentTime) {
        return appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime);
    }

    @Override
    public ResponseEntity<AppointmentResponse> updateAppointment(Long appointmentId, AppointmentStatus appointmentStatus) {
        Appointment appointment = appointmentRepository.getReferenceById(appointmentId);
        appointment.setAppointmentStatus(appointmentStatus);
        appointmentRepository.save(appointment);
        AppointmentResponse appointmentResponse = convertToAppointmentResponse(appointment);
        return ResponseEntity.ok().body(appointmentResponse);
    }

    @Override
    public ResponseEntity<?> getUserAppointments(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID: " + userId + " does not exist"));
        List<Appointment> userAppointments = appointmentRepository.findByUser(user);
        List<AppointmentResponse> userAppointmentsResponse = userAppointments
                .stream().map(this::convertToAppointmentResponse).toList();
        return ResponseEntity.ok(userAppointmentsResponse);
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

    public AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
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
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByLoggedInUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Appointment> userAppointments = appointmentRepository.findByUser(user);
        List<AppointmentResponse> appointmentResponseList = userAppointments
                .stream().map(this::convertToAppointmentResponse).toList();

        return appointmentResponseList;
    }

    @Override
    public Page<AppointmentResponse> getAllApointments(Pageable pageable, String search) {
        AppointmentFilter filter = new AppointmentFilter(search);
        Specification<Appointment> spec = AppointmentSpecification.applyFilters(filter);

        Page<Appointment> appointments = appointmentRepository.findAll(spec, pageable);

        Page<AppointmentResponse> appointmentResponses = appointments
                .map(this::convertToAppointmentResponse);
        return appointmentResponses;
    }
}
