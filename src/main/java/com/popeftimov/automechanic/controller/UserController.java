package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

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

    @GetMapping("admin/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        UserResponse user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hasCars", required = false) Boolean hasCars,
            @RequestParam(value = "hasAppointments", required = false) Boolean hasAppointments,
            Pageable pageable
    ) {
        Page<UserResponse> userResponses = userService.getAllUsers(name, hasCars, hasAppointments, pageable);
        return ResponseEntity.ok(userResponses);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Void> verifyToken() {
        return ResponseEntity.ok().build();
    };
}
