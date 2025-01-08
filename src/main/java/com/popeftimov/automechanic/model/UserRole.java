package com.popeftimov.automechanic.model;

public enum UserRole {

    USER,
    ADMIN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
