package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String phoneNumber;
}
