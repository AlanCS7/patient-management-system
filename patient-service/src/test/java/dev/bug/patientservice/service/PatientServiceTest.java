package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.fixture.PatientFixture;
import dev.bug.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        when(patientRepository.findAll()).thenReturn(Arrays.asList(firstPatient, secondPatient));

        var result = patientService.getPatients();

        assertEquals(
                new PatientResponseDTO(
                        "123e4567-e89b-12d3-a456-426614174000",
                        "John Doe",
                        "john.doe@example.com",
                        "123 Main St",
                        "1990-01-01"),
                result.get(0));
        assertEquals(
                new PatientResponseDTO(
                        "123e4567-e89b-12d3-a456-426614174001",
                        "Jane Doe",
                        "jane.doe@example.com",
                        "456 Oak Ave",
                        "1985-05-15"),
                result.get(1));
        verify(patientRepository).findAll();
    }
}
