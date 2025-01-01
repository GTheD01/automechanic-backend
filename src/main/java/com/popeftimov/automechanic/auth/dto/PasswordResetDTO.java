package com.popeftimov.automechanic.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {
    private String token;
    private String newPassword;
    private String repeatNewPassword;
}
