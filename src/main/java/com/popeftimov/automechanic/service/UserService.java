package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    UserDetails loadUserByUsername(String email);
    int enableUser(String email);
    UserResponse convertToUserResponse(User user);
    ResponseEntity<?> updateUserProfile(Long userId, UserResponse userData);
    void resetPassword(String email, String token, String newPassword, String repeatNewPassword);

    ResponseEntity<List<UserResponse>> getAllUsers();
}
