package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDataDTO {
    private Long totalAppointments;
    private Long totalCars;
    private AppointmentResponse upcomingAppointment;
    private Long totalReports;
    private List<AppointmentsPerYearDTO> appointmentsPerYearCount;
    private List<ReportsPerYearDTO> reportsPerYearCount;
}
