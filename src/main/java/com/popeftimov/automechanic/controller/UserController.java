package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    public UserResponse findCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.convertToUserResponse(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable("userId") Long userId, @RequestBody UserResponse userData) {
        return userService.updateUserProfile(userId, userData);
    }
}
