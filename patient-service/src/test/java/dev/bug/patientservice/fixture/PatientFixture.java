package dev.bug.patientservice.fixture;

import dev.bug.patientservice.model.Patient;

import java.time.LocalDate;
import java.util.UUID;

public final class PatientFixture {

    private PatientFixture() {
    }

    public static Patient johnDoe() {
        return createPatient(
                "123e4567-e89b-12d3-a456-426614174000",
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                LocalDate.of(1990, 1, 1));
    }

    public static Patient janeDoe() {
        return createPatient(
                "123e4567-e89b-12d3-a456-426614174001",
                "Jane Doe",
                "jane.doe@example.com",
                "456 Oak Ave",
                LocalDate.of(1985, 5, 15));
    }

    private static Patient createPatient(String id, String name, String email, String address, LocalDate dateOfBirth) {
        var patient = new Patient();
        patient.setId(UUID.fromString(id));
        patient.setName(name);
        patient.setEmail(email);
        patient.setAddress(address);
        patient.setDateOfBirth(dateOfBirth);
        patient.setRegisteredDate(LocalDate.of(2026, 1, 1));
        return patient;
    }
}
