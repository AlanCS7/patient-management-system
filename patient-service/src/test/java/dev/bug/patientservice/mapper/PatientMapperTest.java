package dev.bug.patientservice.mapper;

import dev.bug.patientservice.fixture.PatientFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PatientMapperTest {

    @Test
    @DisplayName("should map patient to DTO")
    void mapToDTO() {
        var patient = PatientFixture.johnDoe();

        var result = PatientMapper.toDTO(patient);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(patient.getId().toString(), result.id()),
                () -> assertEquals(patient.getName(), result.name()),
                () -> assertEquals(patient.getEmail(), result.email()),
                () -> assertEquals(patient.getAddress(), result.address()),
                () -> assertEquals(patient.getDateOfBirth().toString(), result.dateOfBirth())
        );
    }

    @Test
    @DisplayName("should map patientRequestDTO to model")
    void mapToModel() {
        var patientRequestDTO = PatientFixture.createPatientRequestDTO();

        var result = PatientMapper.toModel(patientRequestDTO);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(patientRequestDTO.name(), result.getName()),
                () -> assertEquals(patientRequestDTO.email(), result.getEmail()),
                () -> assertEquals(patientRequestDTO.address(), result.getAddress()),
                () -> assertEquals(patientRequestDTO.dateOfBirth(), result.getDateOfBirth()),
                () -> assertEquals(patientRequestDTO.registeredDate(), result.getRegisteredDate())
        );
    }
}
