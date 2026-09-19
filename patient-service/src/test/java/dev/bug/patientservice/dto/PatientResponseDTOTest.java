package dev.bug.patientservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PatientResponseDTOTest {

    @Test
    @DisplayName("should create a patient response DTO")
    void createPatientResponseDTO() {
        var patientResponseDTO = new PatientResponseDTO(
                "123e4567-e89b-12d3-a456-426614174000",
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                "1990-01-01");

        assertNotNull(patientResponseDTO);
        assertEquals("123e4567-e89b-12d3-a456-426614174000", patientResponseDTO.id());
        assertEquals("John Doe", patientResponseDTO.name());
        assertEquals("john.doe@example.com", patientResponseDTO.email());
        assertEquals("123 Main St", patientResponseDTO.address());
        assertEquals("1990-01-01", patientResponseDTO.dateOfBirth());
    }
}
