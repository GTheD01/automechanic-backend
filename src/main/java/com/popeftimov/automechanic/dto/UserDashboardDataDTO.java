package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDataDTO {
    private Long totalAppointments;
    private Long totalCars;
    private AppointmentResponse upcomingAppointment;
    private Long totalUserReports;
}
