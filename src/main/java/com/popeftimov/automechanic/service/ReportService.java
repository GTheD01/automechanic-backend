package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.model.Report;

import java.util.List;

public interface ReportService {

    ReportDTO convertReportToReportDTO(Report report);
    ReportDTO createReport(ReportDTO reportData);
    ReportDTO answerUserReport(Long reportId, String answer);
    List<ReportDTO> getLoggedInUserReports();
    List<ReportDTO> getAllReports();
    void deleteUserReport(Long reportId);
}
