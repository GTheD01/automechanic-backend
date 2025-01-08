package com.popeftimov.automechanic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationTokenResponse {
    private String message;

    public ConfirmationTokenResponse(String message) {
        this.message = message;
    }
}
