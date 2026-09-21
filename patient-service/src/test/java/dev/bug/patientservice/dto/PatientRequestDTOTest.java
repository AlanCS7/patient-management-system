package dev.bug.patientservice.dto;

import dev.bug.patientservice.fixture.PatientFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientRequestDTOTest {

    @Test
    @DisplayName("should create a patient request DTO")
    void createPatientRequestDTO() {
        var patientRequestDTO = PatientFixture.createPatientRequestDTO();

        assertAll(
                () -> assertNotNull(patientRequestDTO),
                () -> assertEquals("John Doe", patientRequestDTO.name()),
                () -> assertEquals("john.doe@example.com", patientRequestDTO.email()),
                () -> assertEquals("123 Main St", patientRequestDTO.address()),
                () -> assertEquals("1999-10-26", patientRequestDTO.dateOfBirth()),
                () -> assertEquals("2025-05-10", patientRequestDTO.registeredDate())
        );
    }
}