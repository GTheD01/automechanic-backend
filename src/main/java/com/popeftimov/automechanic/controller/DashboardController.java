package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.UserDashboardDataDTO;
import com.popeftimov.automechanic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardController {

    @Autowired
    private final DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public ResponseEntity<AdminDashboardDataDTO> getAdminDashboardData() {
        AdminDashboardDataDTO adminDashboardData = dashboardService.getAdminDashboardData();

        return ResponseEntity.ok(adminDashboardData);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardDataDTO> getUserDashboardData() {
        UserDashboardDataDTO userDashboardData = dashboardService.getUserDashboardData();
        return ResponseEntity.ok(userDashboardData);
    }
}
