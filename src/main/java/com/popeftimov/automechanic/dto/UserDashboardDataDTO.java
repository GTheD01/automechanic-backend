package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDataDTO {
    private Long totalUserAppointments;
    private Long totalUserCars;
    private AppointmentResponse upcomingUserAppointment;
    private Long totalUserReports;
}
