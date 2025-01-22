package com.popeftimov.automechanic.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class UserPhoneNumberValidator implements Predicate<String> {

    private static final String USER_PHONE_REGEX = "^07[0-9]\\s[0-9]{3}\\s[0-9]{2}\\s[0-9]{2}$";

    @Override
    public boolean test(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return phoneNumber.matches(USER_PHONE_REGEX);
    }
}
