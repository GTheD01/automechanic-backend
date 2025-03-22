package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDetails loadUserByUsername(String email);
    User loadUser(String email);
    User loadUser(Long id);
    void enableUser(String email);
    UserResponse convertToUserResponse(User user);
    UserUpdateProfileResponse updateUserProfile(Long userId, UserUpdateProfileResponse userData);
    void resetPassword(String email, String token, String newPassword, String repeatNewPassword);
    Page<UserResponse> getAllUsers(String name, Boolean hasCars, Boolean hasAppointments, Pageable pageable);
    UserResponse getUser(Long userId);
    void deleteUser(User user);
    void deleteUserById(Long userId);
    UserUpdateProfileResponse updateLoggedInUserProfile(User user, UserUpdateProfileResponse userData);
}
