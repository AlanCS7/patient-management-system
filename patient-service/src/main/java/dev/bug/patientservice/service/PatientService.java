package dev.bug.patientservice.service;

import dev.bug.patientservice.dto.PatientRequestDTO;
import dev.bug.patientservice.dto.PatientResponseDTO;
import dev.bug.patientservice.mapper.PatientMapper;
import dev.bug.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        return patientRepository
                .findAll()
                .stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        var newPatient = PatientMapper.toModel(patientRequestDTO);
        var savedPatient = patientRepository.save(newPatient);
        return PatientMapper.toDTO(savedPatient);
    }
}
