package dev.bug.patientservice.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientTest {

    @Test
    @DisplayName("should create a patient")
    void createPatient() {
        var id = "123e4567-e89b-12d3-a456-426614174000";
        var registeredDate = LocalDate.of(2026, 1, 1);
        var patient = new Patient();
        patient.setId(UUID.fromString(id));
        patient.setName("John Doe");
        patient.setEmail("john.doe@example.com");
        patient.setAddress("123 Main St");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(registeredDate);

        assertEquals(id, patient.getId().toString());
        assertEquals("John Doe", patient.getName());
        assertEquals("john.doe@example.com", patient.getEmail());
        assertEquals("123 Main St", patient.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), patient.getDateOfBirth());
        assertEquals(registeredDate, patient.getRegisteredDate());
    }
}