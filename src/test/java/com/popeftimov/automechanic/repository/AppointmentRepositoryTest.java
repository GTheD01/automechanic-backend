package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.dto.AppointmentsPerYearDTO;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentRepositoryTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Test
    void findByUserOrderByAppointmentDateAscAppointmentTimeAsc() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Smith");

        Appointment appointment1 = new Appointment();
        appointment1.setUser(user);
        appointment1.setAppointmentDate(LocalDate.of(2025, 1, 22));
        appointment1.setAppointmentTime(LocalTime.of(9, 0));

        Appointment appointment2 = new Appointment();
        appointment2.setUser(user);
        appointment2.setAppointmentDate(LocalDate.of(2025, 1, 23));
        appointment2.setAppointmentTime(LocalTime.of(8, 30));

        Appointment appointment3 = new Appointment();
        appointment3.setUser(user);
        appointment3.setAppointmentDate(LocalDate.of(2025, 1, 24));
        appointment3.setAppointmentTime(LocalTime.of(10, 0));

        List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2, appointment3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> mockPage = new PageImpl<>(mockAppointments, pageable, mockAppointments.size());

        when(appointmentRepository.findByUserOrderByAppointmentDateAscAppointmentTimeAsc(user, pageable))
                .thenReturn(mockPage);

        // Act
        Page<Appointment> resultPage = appointmentRepository.findByUserOrderByAppointmentDateAscAppointmentTimeAsc(user, pageable);

        // Assert
        assertNotNull(resultPage);
        assertEquals(3, resultPage.getTotalElements());
        List<Appointment> resultList = resultPage.getContent();
        assertEquals(appointment1, resultList.get(0), "Appointments should be ordered correctly by date and time");
        assertEquals(appointment2, resultList.get(1), "Appointments should be ordered correctly by date and time");
        assertEquals(appointment3, resultList.get(2), "Appointments should be ordered correctly by date and time");

        verify(appointmentRepository, times(1)).findByUserOrderByAppointmentDateAscAppointmentTimeAsc(user, pageable);
    }

    @Test
    void existsByAppointmentDateAndAppointmentTime() {
        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentDate(LocalDate.of(2025, 1, 22));
        appointment1.setAppointmentTime(LocalTime.of(8, 30));

        when(appointmentRepository.existsByAppointmentDateAndAppointmentTime(
                LocalDate.of(2025, 1, 22), LocalTime.of(8, 30)))
                .thenReturn(true);

        // Act
        boolean appointmentExists = appointmentRepository.existsByAppointmentDateAndAppointmentTime(
                LocalDate.of(2025, 1, 22), LocalTime.of(8, 30));

        // Assert
        assertTrue(appointmentExists, "Appointment exists with the given date and time");

        verify(appointmentRepository, times(1)).existsByAppointmentDateAndAppointmentTime(
                LocalDate.of(2025, 1, 22), LocalTime.of(8, 30));

    }

    @Test
    void findFirstByOrderByAppointmentDateAscAppointmentTimeAsc() {
        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentDate(LocalDate.of(2025, 1, 22));
        appointment1.setAppointmentTime(LocalTime.of(8, 30));

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentDate(LocalDate.of(2025, 1, 23));
        appointment2.setAppointmentTime(LocalTime.of(8, 30));

        Appointment appointment3 = new Appointment();
        appointment3.setAppointmentDate(LocalDate.of(2025, 1, 24));
        appointment3.setAppointmentTime(LocalTime.of(8, 30));

        Optional<Appointment> appointmentOptional = Optional.of(appointment1);

        when(appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc())
                .thenReturn(appointmentOptional);

        Optional<Appointment> result = appointmentRepository.findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();

        // Assert
        assertTrue(result.isPresent(), "Appointment not found");
        assertEquals(appointment1, appointmentOptional.get(), "The appointment should be the first one");

        verify(appointmentRepository, times(1)).findFirstByOrderByAppointmentDateAscAppointmentTimeAsc();
    }

    @Test
    void findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Smith");

        Appointment appointment1 = new Appointment();
        appointment1.setUser(user);
        appointment1.setAppointmentDate(LocalDate.of(2025, 1, 22));
        appointment1.setAppointmentTime(LocalTime.of(8, 30));

        Appointment appointment2 = new Appointment();
        appointment2.setUser(user);
        appointment2.setAppointmentDate(LocalDate.of(2025, 1, 23));
        appointment2.setAppointmentTime(LocalTime.of(8, 30));

        Appointment appointment3 = new Appointment();
        appointment3.setUser(user);
        appointment3.setAppointmentDate(LocalDate.of(2025, 1, 24));
        appointment3.setAppointmentTime(LocalTime.of(8, 30));

        Optional<Appointment> appointmentOptional = Optional.of(appointment1);
        when(appointmentRepository.findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user))
            .thenReturn(appointmentOptional);

        Optional<Appointment> result = appointmentRepository.findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user);
        assertTrue(result.isPresent(), "Appointment not found");
        assertEquals(appointment1, appointmentOptional.get(), "The appointment should be the first one");

        verify(appointmentRepository, times(1)).findFirstByUserOrderByAppointmentDateAscAppointmentTimeAsc(user);
    }

    @Test
    void countAppointmentsPerYear() {
        List<AppointmentsPerYearDTO> mockResult = Arrays.asList(
                new AppointmentsPerYearDTO(2025, 2L),
                new AppointmentsPerYearDTO(2024, 1L),
                new AppointmentsPerYearDTO(2023, 3L)
        );

        when(appointmentRepository.countAppointmentsPerYear()).thenReturn(mockResult);

        // Act
        List<AppointmentsPerYearDTO> result = appointmentRepository.countAppointmentsPerYear();

        // Assert
        assertEquals(3, result.size(), "There should be information only for 3 years.");

        AppointmentsPerYearDTO dto2025 = result.stream()
                .filter(dto -> dto.getYear() == 2025).findFirst().orElse(null);
        AppointmentsPerYearDTO dto2024 = result.stream()
                .filter(dto -> dto.getYear() == 2024).findFirst().orElse(null);
        AppointmentsPerYearDTO dto2023 = result.stream()
                .filter(dto -> dto.getYear() == 2023).findFirst().orElse(null);

        assertNotNull(dto2025);
        assertNotNull(dto2024);
        assertNotNull(dto2023);
        assertEquals(2, dto2025.getAppointmentsCount());
        assertEquals(1, dto2024.getAppointmentsCount());
        assertEquals(3, dto2023.getAppointmentsCount());

        verify(appointmentRepository, times(1)).countAppointmentsPerYear();
    }
}