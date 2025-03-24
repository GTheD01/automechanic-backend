package com.popeftimov.automechanic.exception.report;

public class ReportExceptions {

    public static class InvalidReportDescriptionException extends RuntimeException {
        public InvalidReportDescriptionException() {
            super("Description cannot be blank");
        }
    }

    public static class InvalidReportTypeException extends RuntimeException {
        public InvalidReportTypeException() {
            super("Invalid report type");
        }
    }

    public static class ReportTypeNotProvided extends RuntimeException {
        public ReportTypeNotProvided() {
            super("Report type must be provided");
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
