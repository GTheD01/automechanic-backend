package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDetails loadUserByUsername(String email);
    int enableUser(String email);
    UserResponse convertToUserResponse(User user);
    ResponseEntity<?> updateUserProfile(Long userId, UserResponse userData);
    void resetPassword(String email, String token, String newPassword, String repeatNewPassword);

    ResponseEntity<Page<UserResponse>> getAllUsers(String name, Boolean hasCars, Boolean hasAppointments, Pageable pageable);

    ResponseEntity<?> getUser(Long userId);
}
