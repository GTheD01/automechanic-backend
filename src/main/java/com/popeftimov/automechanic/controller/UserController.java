package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.PageMetadata;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.exception.ApiError;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get currently logged in user information",
            description = "Returns the currently logged in user data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/users/me")
    public UserResponse getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.convertToUserResponse(user);
    }

    @Operation(summary = "Delete currently logged in user",
            description = "Deletes the currently logged in user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
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

    @Operation(summary = "Update the currently logged in user information",
            description = "Updates the currently logged in user information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateProfileResponse.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid phone number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PutMapping("/users/me")
    public ResponseEntity<UserUpdateProfileResponse> updateLoggedInUserProfile(@RequestBody UserUpdateProfileResponse userData) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserUpdateProfileResponse userResponse = userService.updateLoggedInUserProfile(user, userData);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Update user profile information",
            description = "Updates the profile information for the user with the provided user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateProfileResponse.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "NOT FOUND - User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad Request - Invalid number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PutMapping("/admin/users/{userId}")
    public ResponseEntity<UserUpdateProfileResponse> updateUserProfile(
                                                                           @PathVariable("userId") Long userId,
                                                                       @RequestBody UserUpdateProfileResponse userData) {
        UserUpdateProfileResponse userResponse = userService.updateUserProfile(userId, userData);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Get user information",
            description = "Returns the profile information for the user with the provided user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "NOT FOUND - User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
    })
    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        UserResponse user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Delete user",
            description = "Deletes the user profile of the user with the provided user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "NOT FOUND - User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
    })
    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users",
            description = "Returns a paginated list of all users. You can filter users by name, whether they have cars, and whether they have appointments."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a paginated list of users.",
            content = @Content(
                    mediaType = "application/json",
                    schemaProperties = {
                            @SchemaProperty(name = "content", array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))),
                            @SchemaProperty(name = "page", schema = @Schema(implementation = PageMetadata.class))
                    }
            )),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Full Authentication required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hasCars", required = false) Boolean hasCars,
            @RequestParam(value = "hasAppointments", required = false) Boolean hasAppointments,
            @ParameterObject Pageable pageable
    ) {
        Page<UserResponse> userResponses = userService.getAllUsers(name, hasCars, hasAppointments, pageable);
        return ResponseEntity.ok(userResponses);
    }
}