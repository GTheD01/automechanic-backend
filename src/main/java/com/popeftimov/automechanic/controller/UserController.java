package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public UserResponse getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.convertToUserResponse(user);
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteCurrentUser(HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.deleteUser(user);

        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserUpdateProfileResponse> updateUserProfile(@PathVariable("userId") Long userId, @RequestBody UserResponse userData) {
        UserUpdateProfileResponse userResponse = userService.updateUserProfile(userId, userData);
        return ResponseEntity.ok(userResponse);
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
