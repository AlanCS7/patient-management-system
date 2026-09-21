package dev.bug.patientservice.mapper;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.model.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public static PatientResponseDTO toDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId().toString(),
                patient.getName(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getDateOfBirth().toString()
        );
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        return new Patient(
                patientRequestDTO.name(),
                patientRequestDTO.email(),
                patientRequestDTO.address(),
                LocalDate.parse(patientRequestDTO.dateOfBirth()),
                LocalDate.parse(patientRequestDTO.registeredDate())
        );
    }
}
