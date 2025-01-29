package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.model.Report;

import java.util.List;

public interface ReportService {
    ReportDTO convertReportToReportDTO(Report report);
    List<ReportDTO> getAllReports();

    ReportDTO createReport(ReportDTO reportData);

    List<ReportDTO> getLoggedInUserReports();

    ReportDTO answerUserReport(Long reportId, String answer);

    void deleteUserReport(Long reportId);
}
