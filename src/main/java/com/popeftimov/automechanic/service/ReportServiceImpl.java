package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.report.ReportExceptions;
import com.popeftimov.automechanic.model.Report;
import com.popeftimov.automechanic.model.ReportType;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<ReportDTO> getAllReports(Pageable pageable) {
        Page<Report> reports = reportRepository.findAll(pageable);
        Page<ReportDTO> reportDTOS = reports.map(this::convertReportToReportDTO);

        return reportDTOS;
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
    public Page<ReportDTO> getLoggedInUserReports(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Report> reportList = reportRepository.findByUser(user, pageable);
        Page<ReportDTO> reportDTOS = reportList.map(this::convertReportToReportDTO);
        return reportDTOS;
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
    public Page<ReportDTO> getUserReports(Long userId, Pageable pageable) {
        User user = userService.loadUser(userId);
        Page<Report> userReports = reportRepository.findByUser(user, pageable);
        Page<ReportDTO> userReportsDTOs = userReports.map(this::convertReportToReportDTO);
        return userReportsDTOs;
    }
}
