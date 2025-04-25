package com.popeftimov.automechanic.exception.car;

public class CarExceptions {

    public static class CarBrandNotFoundException  extends RuntimeException {
        public CarBrandNotFoundException() {
            super("Car brand not found.");
        }
    }

    public static class CarModelNotFoundException  extends RuntimeException {
        public CarModelNotFoundException() {
            super("Car model not found.");
        }
    }

    public static class CarYearInvalidException  extends RuntimeException {
        public CarYearInvalidException() {
            super("Invalid car year. Must be between 1950-2025.");
        }
    }

    public static class CarNotFoundException  extends RuntimeException {
        public CarNotFoundException(Long carId) {
            super("Car with ID: " + carId + " not found");
        }
    }

    public static class CarBrandExistsException  extends RuntimeException {
        public CarBrandExistsException(String brandName) {
            super("Car Brand with name: " + brandName + " already exists");
        }
    }

    public static class CarModelExistsException  extends RuntimeException {
        public CarModelExistsException(String modelName) {
            super("Car Model with name: " + modelName + " already exists");
        }
    }

    public static class CarBrandNotProvidedException  extends RuntimeException {
        public CarBrandNotProvidedException() {
            super("Car Brand must be provided.");
        }
    }
}
