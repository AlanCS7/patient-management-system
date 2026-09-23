package dev.bug.patientservice.dto;

import dev.bug.patientservice.fixture.PatientFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientUpdateRequestDTOTest {

    @Test
    @DisplayName("should create a patient update request DTO")
    void createPatientUpdateRequestDTO() {
        var patientUpdateRequestDTO = PatientFixture.createPatientUpdateRequestDTO();

        assertAll(
                () -> assertNotNull(patientUpdateRequestDTO),
                () -> assertEquals("John Doe Jr", patientUpdateRequestDTO.name()),
                () -> assertEquals("john.doe.jr@example.com", patientUpdateRequestDTO.email()),
                () -> assertEquals("901 Oak Ave", patientUpdateRequestDTO.address()),
                () -> assertEquals(LocalDate.of(1999, 10, 26), patientUpdateRequestDTO.dateOfBirth())
        );
    }
}
