package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserDetails loadUserByUsername(String email);
    Optional<User> findOptionalUserByEmail(String email);
    User loadUser(String email);
    User loadUser(Long id);
    UserResponse getUserResponseByUserId(Long userId);
    void enableUser(String email);
    UserResponse convertToUserResponse(User user);
    UserUpdateProfileResponse updateUserProfileById(Long userId, UserUpdateProfileResponse userData);
    UserUpdateProfileResponse updateLoggedInUserProfile(User user, UserUpdateProfileResponse userData);
    Page<UserResponse> getAllUsers(String name, Boolean hasCars, Boolean hasAppointments, Pageable pageable);
    void saveUser(User user);
    void deleteUser(User user);
    void deleteUserById(Long userId);
}
