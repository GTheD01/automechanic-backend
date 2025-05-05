package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.validator.UserPhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPhoneNumberValidator userPhoneNumberValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setEnabled(true);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_ifEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("test@example.com");

        assertEquals(user, result);
    }

    @Test
    void loadUserByUsername_shouldThrowException_ifEmailNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("missing@example.com"));
    }

    @Test
    void getUserResponseByUserId_shouldReturnUserResponse_ifUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserResponseByUserId(1L);

        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void updateUserProfileById_shouldUpdateUserAndReturnResponse() {
        UserUpdateProfileResponse updateRequest = new UserUpdateProfileResponse("Jane", "Smith", "test@example.com", "0987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPhoneNumberValidator.test("0987654321")).thenReturn(true);

        UserUpdateProfileResponse response = userService.updateUserProfileById(1L, updateRequest);

        assertEquals("Jane", response.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfileById_shouldThrowException_ifPhoneNumberInvalid() {
        UserUpdateProfileResponse updateRequest = new UserUpdateProfileResponse("Jane", "Smith", "test@example.com", "invalid");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPhoneNumberValidator.test("invalid")).thenReturn(false);

        assertThrows(UserExceptions.InvalidPhoneNumberException.class,
                () -> userService.updateUserProfileById(1L, updateRequest));
    }

    @Test
    void deleteUserById_shouldDeleteUser_ifExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserById_shouldThrowException_ifUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserExceptions.UserNotFoundException.class,
                () -> userService.deleteUserById(999L));
    }

    @Test
    void enableUser_shouldCallRepository() {
        String email = "test@example.com";

        userService.enableUser(email);

        verify(userRepository).enableUser(email);
    }

    @Test
    void findOptionalUserByEmail_shouldReturnUser_ifExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findOptionalUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void saveUser_shouldCallSaveOnRepository() {
        userService.saveUser(user);

        verify(userRepository).save(user);
    }

    @Test
    void convertToUserResponse_shouldMapCorrectly() {
        UserResponse response = userService.convertToUserResponse(user);

        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getUserRole(), response.getUserRole());
    }

    @Test
    void updateLoggedInUserProfile_shouldUpdateUser() {
        UserUpdateProfileResponse updateRequest = new UserUpdateProfileResponse("Jane", "Smith", "test@example.com", "0987654321");

        when(userPhoneNumberValidator.test("0987654321")).thenReturn(true);

        UserUpdateProfileResponse result = userService.updateLoggedInUserProfile(user, updateRequest);

        assertEquals("Jane", result.getFirstName());
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_shouldReturnPageOfUserResponses() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("loggedin@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        Page<UserResponse> result = userService.getAllUsers("John", true, true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());

        ArgumentCaptor<Specification<User>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(userRepository).findAll(specCaptor.capture(), eq(pageable));
    }
}
