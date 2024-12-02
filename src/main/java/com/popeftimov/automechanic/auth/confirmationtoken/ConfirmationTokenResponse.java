package com.popeftimov.automechanic.auth.confirmationtoken;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationTokenResponse {
    private String confirmationToken;
}
