package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.ReportDTO;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.exception.report.ReportExceptions;
import com.popeftimov.automechanic.model.Report;
import com.popeftimov.automechanic.model.ReportType;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User mockUser;
    private Report mockReport;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");

        mockReport = new Report();
        mockReport.setId(1L);
        mockReport.setUser(mockUser);
        mockReport.setDescription("Sample report");
        mockReport.setReportType(ReportType.WEBSITE);
        mockReport.setCreatedAt(LocalDateTime.now());

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        lenient().when(context.getAuthentication()).thenReturn(auth);
        lenient().when(auth.getPrincipal()).thenReturn(mockUser);

        SecurityContextHolder.setContext(context);
    }


    @Test
    void createReport_success() {
        ReportDTO reportDTO = new ReportDTO(null, "Description", null, ReportType.WEBSITE, null, null);

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        when(userService.convertToUserResponse(mockUser)).thenReturn(new UserResponse());

        ReportDTO result = reportService.createReport(reportDTO);

        assertNotNull(result);
        assertEquals("Description", result.getDescription());
        assertEquals(ReportType.WEBSITE, result.getReportType());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void createReport_shouldThrowException_whenDescriptionIsEmpty() {
        ReportDTO reportDTO = new ReportDTO(null, "   ", null, ReportType.OTHER, null, null);

        assertThrows(ReportExceptions.InvalidReportDescriptionException.class,
                () -> reportService.createReport(reportDTO));
    }

    @ParameterizedTest
    @EnumSource(ReportType.class)
    void createReport_shouldHandleAllReportTypes(ReportType reportType) {
        ReportDTO reportDTO = new ReportDTO(null, "Description", null, reportType, null, null);

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        when(userService.convertToUserResponse(mockUser)).thenReturn(new UserResponse());

        ReportDTO result = reportService.createReport(reportDTO);

        assertNotNull(result);
        assertEquals(reportType, result.getReportType());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void getAllReports_returnsMappedReportDTOs() {
        Page<Report> reportPage = new PageImpl<>(List.of(mockReport));
        when(reportRepository.findAll(any(Pageable.class))).thenReturn(reportPage);
        when(userService.convertToUserResponse(mockUser)).thenReturn(new UserResponse());

        Page<ReportDTO> result = reportService.getAllReports(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(reportRepository).findAll(any(Pageable.class));
    }

    @Test
    void answerUserReport_success() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(mockReport));
        when(userService.convertToUserResponse(mockUser)).thenReturn(new UserResponse());

        ReportDTO result = reportService.answerUserReport(1L, "Answered!");

        assertEquals("Answered!", result.getAnswer());
        verify(reportRepository).save(mockReport);
    }

    @Test
    void answerUserReport_shouldThrowException_whenAlreadyAnswered() {
        mockReport.setAnswer("Already answered");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(mockReport));

        assertThrows(ReportExceptions.ReportAlreadyAnsweredException.class,
                () -> reportService.answerUserReport(1L, "New answer"));
    }

    @Test
    void deleteUserReport_success() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(mockReport));

        reportService.deleteUserReport(1L);

        verify(reportRepository).delete(mockReport);
    }

    @Test
    void deleteUserReport_shouldThrowException_whenReportNotFound() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ReportExceptions.ReportNotFoundException.class,
                () -> reportService.deleteUserReport(999L));
    }

    @Test
    void getLoggedInUserReports_returnsMappedReports() {
        Page<Report> page = new PageImpl<>(List.of(mockReport));
        when(reportRepository.findByUser(eq(mockUser), any(Pageable.class))).thenReturn(page);
        when(userService.convertToUserResponse(mockUser)).thenReturn(new UserResponse());

        Page<ReportDTO> result = reportService.getLoggedInUserReports(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(reportRepository).findByUser(eq(mockUser), any(Pageable.class));
    }

    @Test
    void getUserReports_returnsReportsForSpecificUser() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setEmail("another@example.com");

        when(userService.loadUser(99L)).thenReturn(anotherUser);
        when(reportRepository.findByUser(eq(anotherUser), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockReport)));
        when(userService.convertToUserResponse(any())).thenReturn(new UserResponse());

        Page<ReportDTO> result = reportService.getUserReports(99L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(userService).loadUser(99L);
        verify(reportRepository).findByUser(eq(anotherUser), any(Pageable.class));
    }
}
