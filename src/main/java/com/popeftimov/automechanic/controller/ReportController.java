package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.ReportAnswerDTO;
import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/admin/reports")
    public ResponseEntity<?> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/reports")
    public ResponseEntity<?> createReport(@RequestBody ReportDTO reportData) {
        ReportDTO createdReportDTO = reportService.createReport(reportData);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdReportDTO.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdReportDTO);
    }

    @GetMapping("/reports/me")
    public ResponseEntity<?> getLoggedInUserReports() {
        List<ReportDTO> reports = reportService.getLoggedInUserReports();
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/admin/reports/{reportId}")
    public ResponseEntity<?> answerUserReport(@PathVariable("reportId") Long reportId, @RequestBody ReportAnswerDTO reportAnswerDTO) {
        ReportDTO reportDTO = reportService.answerUserReport(reportId, reportAnswerDTO.getAnswer());

        return ResponseEntity.ok(reportDTO);
    }

    @DeleteMapping("/admin/reports/{reportId}")
    public ResponseEntity<?> deleteUserReport(@PathVariable("reportId") Long reportId) {
        reportService.deleteUserReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
