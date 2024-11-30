package com.popeftimov.automechanic.user;

public enum Role {

    USER,
    ADMIN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
