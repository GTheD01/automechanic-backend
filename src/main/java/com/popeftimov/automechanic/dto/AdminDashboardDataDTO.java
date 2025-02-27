package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDataDTO {
    private Long totalAppointments;
    private Long totalCars;
    private AppointmentResponse upcomingAppointment;
    private Long totalReports;
}
