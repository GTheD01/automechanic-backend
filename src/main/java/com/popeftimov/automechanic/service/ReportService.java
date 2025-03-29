package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {

    ReportDTO convertReportToReportDTO(Report report);
    ReportDTO createReport(ReportDTO reportData);
    ReportDTO answerUserReport(Long reportId, String answer);
    Page<ReportDTO> getLoggedInUserReports(Pageable pageable);
    Page<ReportDTO> getAllReports(Pageable pageable);
    void deleteUserReport(Long reportId);

    Page<ReportDTO> getUserReports(Long userId, Pageable pageable);
}
