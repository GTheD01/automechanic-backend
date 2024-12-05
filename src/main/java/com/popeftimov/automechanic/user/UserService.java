package com.popeftimov.automechanic.user;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails loadUserByUsername(String email);
    int enableUser(String email);
    UserResponse convertToUserResponse(User user);

    void resetPassword(String token, String newPassword);
}
