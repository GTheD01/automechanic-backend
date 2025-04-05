package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.AdminDashboardDataDTO;
import com.popeftimov.automechanic.dto.UserDashboardDataDTO;
import com.popeftimov.automechanic.exception.ApiError;
import com.popeftimov.automechanic.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get admin dashboard data",
            description = "Returns the upcoming appointment, total appointments, total cars and total reports"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/admin/dashboard")
    public ResponseEntity<AdminDashboardDataDTO> getAdminDashboardData() {
        AdminDashboardDataDTO adminDashboardData = dashboardService.getAdminDashboardData();

        return ResponseEntity.ok(adminDashboardData);
    }

    @Operation(summary = "Get logged user dashboard data",
            description = "Returns the user's upcoming appointment, total user appointments, total user cars, and total user reports"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardDataDTO> getUserDashboardData() {
        UserDashboardDataDTO userDashboardData = dashboardService.getUserDashboardData();
        return ResponseEntity.ok(userDashboardData);
    }
}
