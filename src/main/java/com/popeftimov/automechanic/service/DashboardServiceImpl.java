package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.UserDashboardDataDTO;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import com.popeftimov.automechanic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final CarRepository carRepository;
    private final ReportRepository reportRepository;

    public AdminDashboardDataDTO getAdminDashboardData() {
        Long totalAppointments = appointmentRepository.count();
        Long totalCars = carRepository.count();
        Optional<Appointment> upcomingAppointmentOpt = appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();
        Long totalReports = reportRepository.count();

        if (upcomingAppointmentOpt.isEmpty()) {
            return new AdminDashboardDataDTO(totalAppointments, totalCars, null, totalReports);
        }

        Appointment upcomingAppointment = upcomingAppointmentOpt.get();
        AppointmentResponse appointmentResponse = appointmentService.convertToAppointmentResponse(upcomingAppointment);
        return new AdminDashboardDataDTO(totalAppointments,totalCars, appointmentResponse, totalReports);
    }

    @Override
    public UserDashboardDataDTO getUserDashboardData() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long totalAppointments = user.getAppointmentsCount();
        Long totalCars = user.getCarsCount();
        Long totalUserReports = user.getReportsCount();
        Optional<Appointment> upcomingAppointmentOpt = appointmentRepository
                .findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user);
        if (upcomingAppointmentOpt.isEmpty()) {
            return new UserDashboardDataDTO(totalAppointments, totalCars, null, totalUserReports);
        }

        Appointment upcomingAppointment = upcomingAppointmentOpt.get();
        AppointmentResponse appointmentResponse = appointmentService.convertToAppointmentResponse(upcomingAppointment);
        return new UserDashboardDataDTO(totalAppointments,totalCars, appointmentResponse, totalUserReports);
    }
}
