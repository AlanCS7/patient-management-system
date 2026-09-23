package dev.bug.patientservice.fixture;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.dto.PatientUpdateRequestDTO;
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

    public static Patient createdPatient() {
        return createPatient(
                "123e4567-e89b-12d3-a456-426614174002",
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                LocalDate.of(1999, 10, 26));
    }

    public static PatientResponseDTO johnDoeResponse() {
        return createPatientResponse(
                "123e4567-e89b-12d3-a456-426614174000",
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                "1990-01-01");
    }

    public static PatientResponseDTO janeDoeResponse() {
        return createPatientResponse(
                "123e4567-e89b-12d3-a456-426614174001",
                "Jane Doe",
                "jane.doe@example.com",
                "456 Oak Ave",
                "1985-05-15");
    }

    public static PatientResponseDTO createdPatientResponse() {
        return createPatientResponse(
                "123e4567-e89b-12d3-a456-426614174002",
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                "1999-10-26");
    }

    private static Patient createPatient(String id, String name, String email, String address, LocalDate dateOfBirth) {
        var registeredDate = LocalDate.of(2026, 1, 1);
        var patient = new Patient(name, email, address, dateOfBirth, registeredDate);
        patient.setId(UUID.fromString(id));
        return patient;
    }

    private static PatientResponseDTO createPatientResponse(
            String id,
            String name,
            String email,
            String address,
            String dateOfBirth) {
        return new PatientResponseDTO(id, name, email, address, dateOfBirth);
    }

    public static PatientRequestDTO createPatientRequestDTO() {
        return new PatientRequestDTO(
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                LocalDate.of(1999, 10, 26),
                LocalDate.of(2025, 5, 10)
        );
    }

    public static PatientUpdateRequestDTO createPatientUpdateRequestDTO() {
        return new PatientUpdateRequestDTO(
                "John Doe Jr",
                "john.doe.jr@example.com",
                "901 Oak Ave",
                LocalDate.of(1999, 10, 26)
        );
    }
}
