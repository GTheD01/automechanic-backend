package com.popeftimov.automechanic.exception;

public class ReportExceptions {

    public static class InvalidReportDescriptionException extends RuntimeException {
        public InvalidReportDescriptionException() {
            super("Description cannot be blank");
        }
    }

    public static class ReportNotFound extends RuntimeException {
        public ReportNotFound(Long reportId) {
            super("Report with ID: " + reportId + " not found.");
        }
    }

    public static class ReportAlreadyAnswered extends RuntimeException {
        public ReportAlreadyAnswered() {
            super("The report was already answered");
        }
    }
}
