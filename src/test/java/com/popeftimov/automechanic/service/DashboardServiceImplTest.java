package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.UserDashboardDataDTO;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import com.popeftimov.automechanic.repository.CarRepository;
import com.popeftimov.automechanic.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testGetAdminDashboardData_upcomingAppointmentExists() {
        // Arrange
        long totalAppointments = 10;
        long totalCars = 5;
        long totalReports = 3;
        Appointment upcomingAppointment = new Appointment();
        upcomingAppointment.setId(1L);

        // Mocking repository methods
        when(appointmentRepository.count()).thenReturn(totalAppointments);
        when(carRepository.count()).thenReturn(totalCars);
        when(reportRepository.count()).thenReturn(totalReports);
        when(appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc())
                .thenReturn(Optional.of(upcomingAppointment));
        when(appointmentService.convertToAppointmentResponse(upcomingAppointment)).thenReturn(new AppointmentResponse());

        // Act
        AdminDashboardDataDTO result = dashboardService.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertEquals(totalAppointments, result.getTotalAppointments());
        assertEquals(totalCars, result.getTotalCars());
        assertNotNull(result.getUpcomingAppointment());
        assertEquals(totalReports, result.getTotalReports());
    }

    @Test
    void testGetAdminDashboardData_noUpcomingAppointment() {
        // Arrange
        long totalAppointments = 10;
        long totalCars = 5;
        long totalReports = 3;

        // Mocking repository methods
        when(appointmentRepository.count()).thenReturn(totalAppointments);
        when(carRepository.count()).thenReturn(totalCars);
        when(reportRepository.count()).thenReturn(totalReports);
        when(appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc())
                .thenReturn(Optional.empty());

        // Act
        AdminDashboardDataDTO result = dashboardService.getAdminDashboardData();

        // Assert
        assertNotNull(result);
        assertEquals(totalAppointments, result.getTotalAppointments());
        assertEquals(totalCars, result.getTotalCars());
        assertNull(result.getUpcomingAppointment());
        assertEquals(totalReports, result.getTotalReports());
    }

    @Test
    void testGetUserDashboardData_upcomingAppointmentExists() {
        // Arrange
        long totalAppointments = 5;
        long totalCars = 3;
        long totalUserReports = 2;
        Appointment upcomingAppointment = new Appointment();
        upcomingAppointment.setId(1L);

        // Mocking repository methods
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getAppointmentsCount()).thenReturn(totalAppointments);
        when(user.getCarsCount()).thenReturn(totalCars);
        when(user.getReportsCount()).thenReturn(totalUserReports);
        when(appointmentRepository.findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user))
                .thenReturn(Optional.of(upcomingAppointment));
        when(appointmentService.convertToAppointmentResponse(upcomingAppointment)).thenReturn(new AppointmentResponse());

        // Act
        UserDashboardDataDTO result = dashboardService.getUserDashboardData();
        // Assert
        assertNotNull(result);
        assertEquals(totalAppointments, result.getTotalUserAppointments());
        assertEquals(totalCars, result.getTotalUserCars());
        assertNotNull(result.getUpcomingUserAppointment());
        assertEquals(totalUserReports, result.getTotalUserReports());
    }

    @Test
    void testGetUserDashboardData_noUpcomingAppointment() {
        // Arrange
        long totalAppointments = 5;
        long totalCars = 3;
        long totalUserReports = 2;

        // Mocking repository methods
        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getAppointmentsCount()).thenReturn(totalAppointments);
        when(user.getCarsCount()).thenReturn(totalCars);
        when(user.getReportsCount()).thenReturn(totalUserReports);
        when(appointmentRepository.findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user))
                .thenReturn(Optional.empty());

        // Act
        UserDashboardDataDTO result = dashboardService.getUserDashboardData();

        // Assert
        assertNotNull(result);
        assertEquals(totalAppointments, result.getTotalUserAppointments());
        assertEquals(totalCars, result.getTotalUserCars());
        assertNull(result.getUpcomingUserAppointment());
        assertEquals(totalUserReports, result.getTotalUserReports());
    }
}