package com.popeftimov.automechanic.appointment;

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
}
