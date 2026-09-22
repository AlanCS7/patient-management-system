package dev.bug.patientservice.exception;

public record FieldErrorResponse(
        String field,
        String message
) {
}
