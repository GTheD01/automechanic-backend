package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserDetails loadUserByUsername(String email);
    int enableUser(String email);
    UserResponse convertToUserResponse(User user);
    ResponseEntity<?> updateUserProfile(Long userId, UserResponse userData);
    void resetPassword(String email, String token, String newPassword, String repeatNewPassword);
}
