package com.popeftimov.automechanic.user;

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
    ResponseEntity<?> uploadAvatar(@PathVariable("userId") Long userId, @RequestParam("avatar") MultipartFile avatarFile) throws IOException;
    ResponseEntity<?> updateUserProfile(Long userId, UserResponse userData);
    void resetPassword(String email, String token, String newPassword, String repeatNewPassword);
}
