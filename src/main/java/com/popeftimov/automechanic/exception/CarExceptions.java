package com.popeftimov.automechanic.exception;

public class CarExceptions {

    public static class CarBrandNotFound  extends RuntimeException {
        public CarBrandNotFound() {
            super("Car brand not found.");
        }
    }

    public static class CarModelNotFound  extends RuntimeException {
        public CarModelNotFound() {
            super("Car model not found.");
        }
    }

    public static class CarYearInvalid  extends RuntimeException {
        public CarYearInvalid() {
            super("Invalid car year. Must be between 1950-2025.");
        }
    }
}
