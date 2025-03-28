package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.ReportAnswerDTO;
import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/reports")
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportData) {
        ReportDTO createdReportDTO = reportService.createReport(reportData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReportDTO);
    }

    @GetMapping("/reports/me")
    public ResponseEntity<Page<ReportDTO>> getLoggedInUserReports(Pageable pageable) {
        Page<ReportDTO> reports = reportService.getLoggedInUserReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/reports")
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/reports/user/{userId}")
    public ResponseEntity<Page<ReportDTO>> getUserReports(@PathVariable("userId") Long userId, Pageable pageable) {
        Page<ReportDTO> userReports = reportService.getUserReports(userId, pageable);
        return ResponseEntity.ok(userReports);
    }

    @PutMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportDTO> answerUserReport(@PathVariable("reportId") Long reportId, @RequestBody ReportAnswerDTO reportAnswerDTO) {
        ReportDTO reportDTO = reportService.answerUserReport(reportId, reportAnswerDTO.getAnswer());

        return ResponseEntity.ok(reportDTO);
    }

    @DeleteMapping("/admin/reports/{reportId}")
    public ResponseEntity<Void> deleteUserReport(@PathVariable("reportId") Long reportId) {
        reportService.deleteUserReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
