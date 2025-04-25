package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AppointmentRequest;
import com.popeftimov.automechanic.dto.AppointmentResponse;
import com.popeftimov.automechanic.exception.appointment.AppointmentExceptions;
import com.popeftimov.automechanic.exception.car.CarExceptions;
import com.popeftimov.automechanic.model.Appointment;
import com.popeftimov.automechanic.model.AppointmentStatus;
import com.popeftimov.automechanic.model.Car;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.AppointmentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserService userService;

    @Mock
    private CarService carService;

    @Spy
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private AppointmentRequest createMockAppointmentRequest(LocalDate date, LocalTime time, Long carId) {
        AppointmentRequest appointmentRequest = new AppointmentRequest();
        appointmentRequest.setAppointmentDate(date);
        appointmentRequest.setAppointmentTime(time);
        appointmentRequest.setDescription("Oil change");
        appointmentRequest.setCarId(carId);
        return appointmentRequest;
    }

    @Nested
    class CreateAppointmentTests {
        @Test
        void createAppointment_shouldCreateSuccessfully() {
            LocalDate appointmentDate = LocalDate.now().plusDays(1);
            LocalTime appointmentTime = LocalTime.of(12, 0);
            AppointmentRequest request = createMockAppointmentRequest(appointmentDate, appointmentTime, 1L);

            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setEmail("user@email.com");

            Car mockCar = new Car();
            mockCar.setId(1L);

            Appointment mockAppointment = new Appointment();
            mockAppointment.setUser(mockUser);
            mockAppointment.setAppointmentDate(appointmentDate);
            mockAppointment.setAppointmentTime(appointmentTime);
            mockAppointment.setDescription("Oil change");
            mockAppointment.setAppointmentStatus(AppointmentStatus.UPCOMING);
            mockAppointment.setCar(mockCar);

            AppointmentResponse mockResponse = new AppointmentResponse();

            when(appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime))
                    .thenReturn(false);
            when(carService.findCar(1L)).thenReturn(mockCar);
            when(userService.loadUser(anyString())).thenReturn(mockUser);
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

            // Mock the convertToAppointmentResponse method to return the mockResponse
            doReturn(mockResponse).when(appointmentService).convertToAppointmentResponse(any(Appointment.class));

            // Simulate logged in user
            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("user@email.com");
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AppointmentResponse response = appointmentService.createAppointment(request);

            assertNotNull(response);
            verify(appointmentRepository).save(any(Appointment.class));
            verify(appointmentService).convertToAppointmentResponse(any(Appointment.class));
        }

        @Test
        void createAppointment_shouldThrowExceptionWhenAppointmentIsInThePast() {
            LocalDate appointmentDate = LocalDate.now().minusDays(1);
            LocalTime appointmentTime = LocalTime.of(12, 0);

            AppointmentRequest request = createMockAppointmentRequest(appointmentDate, appointmentTime, 1L);

            assertThrows(AppointmentExceptions.AppointmentCannotScheduleInThePastException.class,
                    () -> appointmentService.createAppointment(request));

            verifyNoInteractions(appointmentRepository);
        }

        @Test
        void createAppointment_shouldThrowExceptionWhenAppointmentAlreadyExists() {
            LocalDate appointmentDate = LocalDate.now().plusDays(1);
            LocalTime appointmentTime = LocalTime.of(12, 0);

            AppointmentRequest request = createMockAppointmentRequest(appointmentDate, appointmentTime, 1L);

            when(appointmentRepository.existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime))
                    .thenReturn(true);

            assertThrows(AppointmentExceptions.AppointmentAtDateTimeExistsException.class, () -> appointmentService.createAppointment(request));

            verify(appointmentRepository).existsByAppointmentDateAndAppointmentTime(appointmentDate, appointmentTime);
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        void createAppointment_shouldThrowCarNotFoundExceptionWhenCarDoesNotExist() {
            LocalDate appointmentDate = LocalDate.now().plusDays(1);
            LocalTime appointmentTime = LocalTime.of(12, 0);
            AppointmentRequest request = createMockAppointmentRequest(appointmentDate, appointmentTime, 900L);

            when(carService.findCar(900L)).thenThrow(new CarExceptions.CarNotFoundException(900L));

            assertThrows(CarExceptions.CarNotFoundException.class, () -> appointmentService.createAppointment(request));

            verify(carService, times(1)).findCar(900L);
        }
    }

    @Test
    void isAppointmentAtTimeExists_shouldReturnTrueWhenAppointmentExists() {
        LocalDate date = LocalDate.of(2025, 4, 25);
        LocalTime time = LocalTime.of(12, 0);

        when(appointmentRepository.existsByAppointmentDateAndAppointmentTime(date, time))
                .thenReturn(true);

        boolean exists = appointmentService.isAppointmentAtTimeExists(date, time);

        // Assert
        assertTrue(exists);
        verify(appointmentRepository, times(1)).existsByAppointmentDateAndAppointmentTime(date, time);
    }

    @Test
    void isAppointmentAtTimeExists_shouldReturnFalseWhenAppointmentDoesNotExist() {
        LocalDate date = LocalDate.of(2025, 4, 25);
        LocalTime time = LocalTime.of(12, 0);

        when(appointmentRepository.existsByAppointmentDateAndAppointmentTime(date, time))
                .thenReturn(false);

        boolean exists = appointmentService.isAppointmentAtTimeExists(date, time);

        // Assert
        assertFalse(exists);
        verify(appointmentRepository, times(1)).existsByAppointmentDateAndAppointmentTime(date, time);
    }

    @Test
    void updateAppointment() {
    }

    @Test
    void getUserAppointments() {
    }

    @Test
    void convertToAppointmentResponse() {
    }

    @Test
    void getAppointmentsByLoggedInUser() {
    }

    @Test
    void getAllApointments() {
    }
}