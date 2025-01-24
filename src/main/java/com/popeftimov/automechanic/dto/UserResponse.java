package com.popeftimov.automechanic.dto;

import com.popeftimov.automechanic.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole userRole;
    private String avatar;
    private String phoneNumber;
    private Long carsCount;
    private Long appointmentCount;
    private boolean enabled;
}
