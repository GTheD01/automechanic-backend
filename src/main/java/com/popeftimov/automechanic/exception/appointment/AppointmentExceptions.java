package com.popeftimov.automechanic.exception.appointment;

public class AppointmentExceptions {

    public static class AppointmentAtDateTimeExists extends RuntimeException {
        public AppointmentAtDateTimeExists() {
            super("Appointment already exists at this date and time");
        }
    }

    public static class AppointmentCannotScheduleInThePast extends RuntimeException {
        public AppointmentCannotScheduleInThePast() {
            super("Invalid date/time cannot schedule in the past");
        }
    }

    public static class AppointmentNotFound extends RuntimeException {
        public AppointmentNotFound(Long appointmentId) { super("Appointment with ID: " + appointmentId + " not found."); }
    }

    public static class AppointmentInvalidStatus extends RuntimeException {
        public AppointmentInvalidStatus() { super("Invalid appointment status"); }
    }

    public static class AppointmentNoCarSelected extends RuntimeException {
        public AppointmentNoCarSelected() {
            super("No car selected.");
        }
    }
}
