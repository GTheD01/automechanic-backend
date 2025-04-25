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

    public static class ReportTypeNotProvidedException extends RuntimeException {
        public ReportTypeNotProvidedException() {
            super("Report type must be provided");
        }
    }

    public static class ReportNotFoundException extends RuntimeException {
        public ReportNotFoundException(Long reportId) {
            super("Report with ID: " + reportId + " not found.");
        }
    }

    public static class ReportAlreadyAnsweredException extends RuntimeException {
        public ReportAlreadyAnsweredException() {
            super("The report was already answered");
        }
    }
}
