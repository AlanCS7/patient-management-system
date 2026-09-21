package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.fixture.PatientFixture;
import dev.bug.patientservice.model.Patient;
import dev.bug.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        this.patientService = new PatientService(patientRepository);
    }

    @Test
    @DisplayName("should get patients")
    void getPatients() {
        var firstPatient = PatientFixture.johnDoe();
        var secondPatient = PatientFixture.janeDoe();

        when(patientRepository.findAll()).thenReturn(List.of(firstPatient, secondPatient));

        var result = patientService.getPatients();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(
                        new PatientResponseDTO(
                                "123e4567-e89b-12d3-a456-426614174000",
                                "John Doe",
                                "john.doe@example.com",
                                "123 Main St",
                                "1990-01-01"),
                        result.getFirst()),
                () -> assertEquals(
                        new PatientResponseDTO(
                                "123e4567-e89b-12d3-a456-426614174001",
                                "Jane Doe",
                                "jane.doe@example.com",
                                "456 Oak Ave",
                                "1985-05-15"),
                        result.get(1)));
        verify(patientRepository).findAll();
    }

    @Test
    @DisplayName("should create patient")
    void createPatient() {
        var patientRequestDTO = PatientFixture.createPatientRequestDTO();
        var savedPatient = PatientFixture.createdPatient();

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        var result = patientService.createPatient(patientRequestDTO);

        var savedPatientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(savedPatientCaptor.capture());

        var persistedPatient = savedPatientCaptor.getValue();
        assertAll(
                () -> assertEquals(patientRequestDTO.name(), persistedPatient.getName()),
                () -> assertEquals(patientRequestDTO.email(), persistedPatient.getEmail()),
                () -> assertEquals(patientRequestDTO.address(), persistedPatient.getAddress()),
                () -> assertEquals(patientRequestDTO.dateOfBirth(), persistedPatient.getDateOfBirth().toString()),
                () -> assertEquals(patientRequestDTO.registeredDate(), persistedPatient.getRegisteredDate().toString()),
                () -> assertEquals(
                        new PatientResponseDTO(
                                "123e4567-e89b-12d3-a456-426614174002",
                                "John Doe",
                                "john.doe@example.com",
                                "123 Main St",
                                "1999-10-26"),
                        result)
        );
    }
}
