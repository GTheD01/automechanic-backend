package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
}
