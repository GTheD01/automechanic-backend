package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDataDTO {
    private Long totalAppointments;
    private Long totalCars;
    private AppointmentResponse upcomingAppointment;
}
