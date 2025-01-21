package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final CarRepository carRepository;
    private final AppointmentService appointmentService;

    public AdminDashboardDataDTO getAdminDashboardData() {
        Long totalAppointments = appointmentRepository.count();
        Long totalCars = carRepository.count();
        Optional<Appointment> upcomingAppointmentOpt = appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();

        if (upcomingAppointmentOpt.isEmpty()) {
            AppointmentResponse defaultAppointmentResponse = new AppointmentResponse();
            return new AdminDashboardDataDTO(totalAppointments, totalCars, defaultAppointmentResponse);
        }

        Appointment upcomingAppointment = upcomingAppointmentOpt.get();
        AppointmentResponse appointmentResponse = appointmentService.convertToAppointmentResponse(upcomingAppointment);
        return new AdminDashboardDataDTO(totalAppointments,totalCars, appointmentResponse);
    }
}
