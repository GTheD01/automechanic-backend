package com.popeftimov.automechanic.exception.appointment;

public class AppointmentExceptions {

    public static class AppointmentAtDateTimeExistsException extends RuntimeException {
        public AppointmentAtDateTimeExistsException() {
            super("Appointment already exists at this date and time");
        }
    }

    public static class AppointmentCannotScheduleInThePastException extends RuntimeException {
        public AppointmentCannotScheduleInThePastException() {
            super("Invalid date/time cannot schedule in the past");
        }
    }

    public static class AppointmentNotFoundException extends RuntimeException {
        public AppointmentNotFoundException(Long appointmentId) { super("Appointment with ID: " + appointmentId + " not found."); }
    }

    public static class AppointmentInvalidStatusException extends RuntimeException {
        public AppointmentInvalidStatusException() { super("Invalid appointment status"); }
    }

    public static class AppointmentNoCarSelectedException extends RuntimeException {
        public AppointmentNoCarSelectedException() {
            super("No car selected.");
        }
    }
}
