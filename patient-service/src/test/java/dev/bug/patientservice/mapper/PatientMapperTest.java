package dev.bug.patientservice.mapper;

import dev.bug.patientservice.fixture.PatientFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PatientMapperTest {

    @Test
    @DisplayName("should map patient to DTO")
    void mapToDTO() {
        var patient = PatientFixture.johnDoe();

        var dto = PatientMapper.toDTO(patient);

        assertNotNull(dto);
        assertEquals(dto.id(), patient.getId().toString());
        assertEquals(dto.name(), patient.getName());
        assertEquals(dto.email(), patient.getEmail());
        assertEquals(dto.address(), patient.getAddress());
        assertEquals(dto.dateOfBirth(), patient.getDateOfBirth().toString());
    }
}
