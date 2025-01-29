package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.UserDashboardDataDTO;

public interface DashboardService {
    AdminDashboardDataDTO getAdminDashboardData();

    UserDashboardDataDTO getUserDashboardData();
}
