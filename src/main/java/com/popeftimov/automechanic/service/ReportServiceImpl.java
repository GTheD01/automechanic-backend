package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.report.ReportExceptions;
import com.popeftimov.automechanic.model.Report;
import com.popeftimov.automechanic.model.ReportType;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final UserService userService;

    public ReportDTO convertReportToReportDTO(Report report) {
        UserResponse userResponse = userService.convertToUserResponse(report.getUser());
        return new ReportDTO(
                report.getId(),
                report.getDescription(),
                report.getAnswer(),
                report.getReportType().toString(),
                report.getCreatedAt(),
                userResponse
        );
    }

    @Override
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();

        return reports.stream().map(this::convertReportToReportDTO).toList();
    }

    @Override
    public ReportDTO createReport(ReportDTO reportData) {
        String description = reportData.getDescription();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String reportTypeString = reportData.getReportType();

        if (reportTypeString == null) {
            throw new ReportExceptions.ReportTypeNotProvided();
        }

        ReportType reportType;
        try {
            reportType = ReportType.valueOf(reportTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReportExceptions.InvalidReportTypeException();
        }

        if (description == null || description.trim().isEmpty()) {
            throw new ReportExceptions.InvalidReportDescriptionException();
        }

        Report report = new Report();
        report.setDescription(description);
        report.setUser(user);
        report.setReportType(reportType);
        reportRepository.save(report);

        return this.convertReportToReportDTO(report);
    }

    @Override
    public List<ReportDTO> getLoggedInUserReports() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Report> reportList = reportRepository.findByUser(user);
        return reportList.stream().map(this::convertReportToReportDTO).toList();
    }

    @Override
    public ReportDTO answerUserReport(Long reportId, String answer) {
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new ReportExceptions.ReportNotFound(reportId)
        );
        if (report.getAnswer() != null) {
            throw new ReportExceptions.ReportAlreadyAnswered();
        }

        report.setAnswer(answer);
        reportRepository.save(report);
        return this.convertReportToReportDTO(report);
    }

    @Override
    public void deleteUserReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new ReportExceptions.ReportNotFound(reportId)
        );
        reportRepository.delete(report);
    }

    @Override
    public List<ReportDTO> getUserReports(Long userId) {
        User user = userService.loadUser(userId);
        List<Report> userReports = reportRepository.findByUser(user);
        return userReports.stream().map(this::convertReportToReportDTO).toList();
    }
}
