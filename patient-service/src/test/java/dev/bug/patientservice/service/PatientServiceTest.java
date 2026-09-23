package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientUpdateRequestDTO;
import dev.bug.patientservice.exception.EmailAlreadyExistsException;
import dev.bug.patientservice.exception.PatientNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
                () -> assertEquals(PatientFixture.johnDoeResponse(), result.getFirst()),
                () -> assertEquals(PatientFixture.janeDoeResponse(), result.get(1)));
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
                () -> assertEquals(patientRequestDTO.dateOfBirth(), persistedPatient.getDateOfBirth()),
                () -> assertEquals(patientRequestDTO.registeredDate(), persistedPatient.getRegisteredDate()),
                () -> assertEquals(PatientFixture.createdPatientResponse(), result)
        );
    }

    @Test
    @DisplayName("should reject patient creation when email already exists")
    void createPatientWhenEmailAlreadyExists() {
        var patientRequestDTO = PatientFixture.createPatientRequestDTO();

        when(patientRepository.existsByEmail(patientRequestDTO.email())).thenReturn(true);

        var exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> patientService.createPatient(patientRequestDTO));

        assertEquals("Email address already exists", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("should update patient")
    void updatePatient() {
        var patientUpdateRequestDTO = PatientFixture.createPatientUpdateRequestDTO();
        var savedPatient = PatientFixture.createdPatient();
        var id = savedPatient.getId();

        when(patientRepository.findById(id)).thenReturn(Optional.of(savedPatient));
        when(patientRepository.existsByEmailAndIdNot(patientUpdateRequestDTO.email(), id)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = patientService.updatePatient(id, patientUpdateRequestDTO);

        var updatedPatientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(updatedPatientCaptor.capture());

        var updatedPatient = updatedPatientCaptor.getValue();
        assertAll(
                () -> assertEquals(id.toString(), result.id()),
                () -> assertEquals(patientUpdateRequestDTO.name(), result.name()),
                () -> assertEquals(patientUpdateRequestDTO.email(), result.email()),
                () -> assertEquals(patientUpdateRequestDTO.address(), result.address()),
                () -> assertEquals(patientUpdateRequestDTO.dateOfBirth().toString(), result.dateOfBirth()),
                () -> assertEquals(patientUpdateRequestDTO.name(), updatedPatient.getName()),
                () -> assertEquals(patientUpdateRequestDTO.email(), updatedPatient.getEmail()),
                () -> assertEquals(patientUpdateRequestDTO.address(), updatedPatient.getAddress()),
                () -> assertEquals(patientUpdateRequestDTO.dateOfBirth(), updatedPatient.getDateOfBirth()),
                () -> assertEquals(savedPatient.getId(), updatedPatient.getId()),
                () -> assertEquals(savedPatient.getRegisteredDate(), updatedPatient.getRegisteredDate())
        );
    }

    @Test
    @DisplayName("should reject patient update when email is already used by another patient")
    void updatePatientWhenEmailAlreadyExists() {
        var patient = PatientFixture.createdPatient();
        var patientUpdateRequestDTO = PatientFixture.createPatientUpdateRequestDTO();
        var id = patient.getId();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmailAndIdNot(patientUpdateRequestDTO.email(), id)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> patientService.updatePatient(id, patientUpdateRequestDTO));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("should update patient with the current email")
    void updatePatientWithCurrentEmail() {
        var patient = PatientFixture.createdPatient();
        var patientUpdateRequestDTO = new PatientUpdateRequestDTO(
                patient.getName(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getDateOfBirth()
        );
        var id = patient.getId();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmailAndIdNot(patient.getEmail(), id)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        patientService.updatePatient(id, patientUpdateRequestDTO);

        verify(patientRepository).existsByEmailAndIdNot(patient.getEmail(), id);
        verify(patientRepository).save(patient);
    }

    @Test
    @DisplayName("should reject update when patient does not exist")
    void updatePatientWhenPatientDoesNotExist() {
        var id = PatientFixture.createdPatient().getId();
        var patientUpdateRequestDTO = PatientFixture.createPatientUpdateRequestDTO();

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                PatientNotFoundException.class,
                () -> patientService.updatePatient(id, patientUpdateRequestDTO));

        assertEquals("Patient with ID %s was not found".formatted(id), exception.getMessage());
        verify(patientRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("should delete patient")
    void deletePatient() {
        var patient = PatientFixture.createdPatient();
        var id = patient.getId();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        patientService.deletePatient(id);

        verify(patientRepository).delete(patient);
    }

    @Test
    @DisplayName("should reject delete when patient does not exist")
    void deletePatientWhenPatientDoesNotExist() {
        var id = PatientFixture.createdPatient().getId();

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                PatientNotFoundException.class,
                () -> patientService.deletePatient(id));

        assertEquals("Patient with ID %s was not found".formatted(id), exception.getMessage());
        verify(patientRepository, never()).delete(any(Patient.class));
    }
}
