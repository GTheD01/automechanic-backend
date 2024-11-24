package com.popeftimov.automechanic.auth.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class PasswordValidator implements Predicate<String> {

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

    @Override
    public boolean test(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.matches(PASSWORD_REGEX);
    }
}
