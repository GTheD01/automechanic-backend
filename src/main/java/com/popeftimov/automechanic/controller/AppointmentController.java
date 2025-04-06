package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.dto.AppointmentUpdateRequest;
import com.popeftimov.automechanic.dto.PageMetadata;
import com.popeftimov.automechanic.exception.ApiError;
import com.popeftimov.automechanic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Get all appointments",
            description = "Returns a paginated list of all appointments. Optionally, you can filter appointments by the user's name or the appointment's description."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a paginated list of appointments.",
                    content = @Content(
                            mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class))),
                                    @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                            }
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/admin/appointments")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @ParameterObject Pageable pageable,
            @RequestParam(value = "search", required = false) String search
    ) {
        Page<AppointmentResponse> appointmentPage = appointmentService.getAllApointments(pageable, search);
        return ResponseEntity.ok().body(appointmentPage);
    }

    @Operation(summary = "Create appontment", description = "Creates a new appointment. Ensure the appointment time is in the future, and a car is selected before scheduling.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the appointment",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponse.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - The appointment cannot be scheduled in the past, or the appointment conflicts with another. Ensure a car is selected.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        AppointmentResponse appointment = appointmentService.createAppointment(appointmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointment);
    }

    @Operation(summary = "Get currently logged in user appointments",
            description = "Returns the currently logged in user appointments"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(
                    mediaType = "application/json",
                    schemaProperties = {
                            @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class))),
                            @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                    }
            )),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/appointments/me")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByLoggedInUser(@ParameterObject Pageable pageable) {
        Page<AppointmentResponse> appointments = appointmentService.getAppointmentsByLoggedInUser(pageable);

        return ResponseEntity.ok().body(appointments);
    }

    @Operation(
            summary = "Get appointments for a specific user",
            description = "Returns a paginated list of appointments for a specific user identified by their user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the paginated list of appointments for the user.",
                    content = @Content(
                            mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class))),
                                    @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Full authentication is required to access this resource.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - The user with the specified ID does not exist.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/admin/appointments/{userId}")
    public ResponseEntity<Page<AppointmentResponse>> getUserAppointments(@PathVariable("userId") Long userId, @ParameterObject Pageable pageable) {
        Page<AppointmentResponse> userAppointments = appointmentService.getUserAppointments(userId, pageable);
        return ResponseEntity.ok().body(userAppointments);
    }

    @Operation(
            summary = "Update an appointment",
            description = "Updates the status of an existing appointment identified by its appointment ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated the appointment status.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Appointment not found or invalid appointment status provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Full authentication is required to access this resource.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found - The appointment with the specified ID does not exist.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PutMapping("/admin/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable("appointmentId") Long appointmentId,
                                                                 @RequestBody AppointmentUpdateRequest appointmentUpdateRequest) {

        AppointmentResponse appointmentResponse = appointmentService.updateAppointment(appointmentId, appointmentUpdateRequest);
        return ResponseEntity.ok(appointmentResponse);
    }
}
