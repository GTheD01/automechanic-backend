package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.PageMetadata;
import com.popeftimov.automechanic.dto.ReportAnswerDTO;
import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.exception.ApiError;
import com.popeftimov.automechanic.service.ReportService;
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
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Create report", description = "Creates new report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReportDTO.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid or missing report type, invalid report description, or other validation failures.",
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
    @PostMapping("/reports")
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportData) {
        ReportDTO createdReportDTO = reportService.createReport(reportData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReportDTO);
    }

    @Operation(summary = "Get currently logged in user reports",
            description = "Returns the currently logged in user reports"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(
                    mediaType = "application/json",
                    schemaProperties = {
                            @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = ReportDTO.class))),
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
    @GetMapping("/reports/me")
    public ResponseEntity<Page<ReportDTO>> getLoggedInUserReports(@ParameterObject Pageable pageable) {
        Page<ReportDTO> reports = reportService.getLoggedInUserReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get all reports",
            description = "Returns a paginated list of all reports."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a paginated list of reports.",
                    content = @Content(
                            mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = ReportDTO.class))),
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
    @GetMapping("/admin/reports")
    public ResponseEntity<Page<ReportDTO>> getAllReports(@ParameterObject Pageable pageable) {
        Page<ReportDTO> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get user reports",
            description = "Returns a paginated list of all user reports."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a paginated list of the user's reports.",
                    content = @Content(
                            mediaType = "application/json",
                            schemaProperties = {
                                    @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = ReportDTO.class))),
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
    @GetMapping("/admin/reports/user/{userId}")
    public ResponseEntity<Page<ReportDTO>> getUserReports(@PathVariable("userId") Long userId, @ParameterObject Pageable pageable) {
        Page<ReportDTO> userReports = reportService.getUserReports(userId, pageable);
        return ResponseEntity.ok(userReports);
    }

    @Operation(summary = "Answer user report",
            description = "Returns the newly report data with the answer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully answered user report.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReportDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Report already answered",
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
            ),
            @ApiResponse(responseCode = "404",
                    description = "NOT FOUND - Report not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
    })
    @PutMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportDTO> answerUserReport(@PathVariable("reportId") Long reportId, @RequestBody ReportAnswerDTO reportAnswerDTO) {
        ReportDTO reportDTO = reportService.answerUserReport(reportId, reportAnswerDTO.getAnswer());

        return ResponseEntity.ok(reportDTO);
    }

    @Operation(summary = "Delete report",
            description = "Deletes the report with the provided report ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "NOT FOUND - Report not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
    })
    @DeleteMapping("/admin/reports/{reportId}")
    public ResponseEntity<Void> deleteUserReport(@PathVariable("reportId") Long reportId) {
        reportService.deleteUserReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
